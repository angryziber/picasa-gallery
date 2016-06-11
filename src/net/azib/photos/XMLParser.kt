package net.azib.photos

import java.io.InputStream
import java.util.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants.*
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

class XMLParser<T>(private val listener: XMLListener<T>) {
  private var characters = ""
  private val path = LinkedList<String>()
  private var rootFound = false

  fun parse(xml: InputStream): T = xml.use {
    val reader = XMLInputFactory.newInstance().createXMLEventReader(xml)
    try {
      while (reader.hasNext()) {
        val event = reader.nextEvent()
        when (event.eventType) {
          START_ELEMENT -> startElement(event)
          CHARACTERS -> characters(event)
          END_ELEMENT -> endElement(event)
        }
      }
    }
    catch (e: XMLListener.StopParse) {
    }
    finally {
      reader.close()
    }
    return listener.result
  }


  private fun startElement(event: XMLEvent) {
    characters = ""
    val startElement = event.asStartElement()
    val name = startElement.name.localPart
    if (!rootFound) {
      rootFound = true
      listener.rootElement(name)
      parseAttributes(startElement, "")
      return
    }
    path.addLast(name)
    val prefix = path.joinToString("/")
    listener.start(prefix)
    parseAttributes(startElement, prefix)
  }

  private fun parseAttributes(element: StartElement, prefix: String) {
    @Suppress("UNCHECKED_CAST")
    val attributes = element.attributes as Iterator<Attribute>
    while (attributes.hasNext()) {
      val attribute = attributes.next()
      listener.value(prefix + "@" + attribute.name, attribute.value)
    }
  }

  private fun characters(event: XMLEvent) {
    characters += event.asCharacters().data
  }

  private fun endElement(event: XMLEvent) {
    characters = characters.trim()
    if (!path.isEmpty()) {
      val p = path.joinToString("/")
      if (!characters.isEmpty()) {
        listener.value(p, characters)
        characters = ""
      }
      listener.end(p)
    }
    else {
      val endElement = event.asEndElement()
      listener.rootElementEnd(endElement.name.localPart)
    }
    path.pollLast()
  }
}

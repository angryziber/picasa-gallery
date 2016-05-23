package net.azib.photos;

import com.google.common.base.Joiner;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.Iterator;

import static com.google.common.collect.Lists.newLinkedList;
import static javax.xml.stream.XMLStreamConstants.*;

public class XMLParser<T> {
  private String characters = "";
  private final Deque<String> path = newLinkedList();
  private boolean rootFound;
  private final XMLListener<T> listener;

  public XMLParser(XMLListener<T> listener) {
    this.listener = listener;
  }

  public T parse(InputStream xml) {
    try {
      XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(xml);

      try {
        while (reader.hasNext()) {
          XMLEvent event = reader.nextEvent();
          switch (event.getEventType()) {
            case START_ELEMENT:
              startElement(event);
              break;
            case CHARACTERS:
              characters(event);
              break;
            case END_ELEMENT:
              endElement(event);
          }
        }
      }
      catch (XMLListener.StopParse e) {
        e.printStackTrace();
      }
      finally {
        reader.close();
      }
    }
    catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
    finally {
      closeQuietly(xml);
    }
    return listener.getResult();
  }

  public static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) closeable.close();
    } catch (IOException ignore) {
    }
  }


  private void startElement(XMLEvent event) throws XMLListener.StopParse {
    characters = "";
    StartElement startElement = event.asStartElement();
    String name = startElement.getName().getLocalPart();
    if (!rootFound) {
      rootFound = true;
      listener.rootElement(name);
      parseAttributes(startElement, "");
      return;
    }
    path.addLast(name);
    String prefix = Joiner.on("/").join(path);
    listener.start(prefix);
    parseAttributes(startElement, prefix);
  }

  private void parseAttributes(StartElement element, String prefix) throws XMLListener.StopParse {
    for (Iterator<Attribute> attributes = element.getAttributes(); attributes.hasNext(); ) {
      Attribute attribute = attributes.next();
      listener.value(prefix + "@" + attribute.getName(), attribute.getValue());
    }
  }

  private void characters(XMLEvent event) {
    characters += event.asCharacters().getData();
  }

  private void endElement(XMLEvent event) throws XMLListener.StopParse {
    characters = characters.trim();
    if (!path.isEmpty()) {
      String p = Joiner.on("/").join(path);
      if (!characters.isEmpty()) {
        listener.value(p, characters);
        characters = "";
      }
      listener.end(p);
    }
    else {
      EndElement endElement = event.asEndElement();
      listener.rootElementEnd(endElement.getName().getLocalPart());
    }
    path.pollLast();
  }
}

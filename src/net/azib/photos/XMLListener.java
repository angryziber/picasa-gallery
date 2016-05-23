package net.azib.photos;

public interface XMLListener<T> {
  class StopParse extends Exception {}

  T getResult();

  void rootElement(String name);

  void rootElementEnd(String name);

  void value(String path, String value) throws StopParse;

  void start(String path) throws StopParse;

  void end(String path) throws StopParse;
}

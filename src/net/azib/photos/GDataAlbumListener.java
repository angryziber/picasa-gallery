package net.azib.photos;

public class GDataAlbumListener implements XMLListener<Album> {
  private Album album = new Album();

  @Override public Album getResult() {
    return album;
  }

  @Override
  public void rootElement(String name) {
  }

  @Override
  public void rootElementEnd(String name) {
  }

  @Override
  public void value(String path, String value) throws StopParse {
  }

  @Override
  public void start(String path) throws StopParse {
  }

  @Override
  public void end(String path) throws StopParse {
  }
}

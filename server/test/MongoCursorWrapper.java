package server.test;

import com.mongodb.client.MongoCursor;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Allows for mocking of a MongoCursor with a simple {@see Iterable}
 */
public class MongoCursorWrapper<T> implements MongoCursor<T> {
  private Iterator<T> wrapped;
  private boolean closed;

  public MongoCursorWrapper(Iterator<T> wrapped) {
    closed = false;
    this.wrapped = wrapped;
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public ServerAddress getServerAddress() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServerCursor getServerCursor() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasNext() {
    if (!closed) {
      return wrapped.hasNext();
    } else {
      return false;
    }
  }

  @Override
  public T next() {
    if (!closed) {
      return wrapped.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public T tryNext() {
    throw new UnsupportedOperationException();
  }
}

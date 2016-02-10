package main.java.Subscribers;

/**
 * Created by jethva on 2/8/16.
 */
public class ConsoleSubscriber implements ISubscriber {

  public void onPublished(String message) {
    System.out.println(message);
  }
}

package scraper;

/**
 * Created by jethva on 2/1/16.
 */
public interface Subscriber<E> {
    void onPublished(E result);
}

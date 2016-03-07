package server.app;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import java.util.Collection;


public class Utils{

  public static void setupPaginator(FindIterable<Document> iter, int range_start, int range_end) {
    // get results in range
    if (range_start == range_end) {
      iter.skip(range_start);
      iter.limit(1);
    } else if (range_start == 0) {
      iter.limit(range_end);
    } else {
      iter.skip(range_start);
      iter.limit(range_end - 1);
    }
  }

  public static int sizeof(Iterable<?> it) {
    if (it instanceof Collection)
      return ((Collection<?>)it).size();
    // else iterate
    int i = 0;
    for (Object obj : it) i++;
    return i;
  }

}

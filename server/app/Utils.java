package server.app;

import com.mongodb.client.FindIterable;
import org.bson.Document;

public class Utils {

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
}

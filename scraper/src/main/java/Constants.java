package main.java;

import java.io.File;

/**
 * Created by mayank on 2/9/16.
 */
public class Constants {

    public static final class Mongo {
        public static final String HOST = "localhost";
        public static final int PORT = 27017;
        public static final String DATABASE = "intelligeatsa";
        public static final String RECIPES_COLLECTION = "recipes";
        public static final String USER = "intelligeatsaUser";
        public static final char[] USER_PASS = "12345678".toCharArray();
    }

    public static final class Scraper {
        public static final String URLS_FILE_PATH_PROMPT = "Urls File Path:";
        public static final String NYTIMES_BASE_URL = "http://cooking.nytimes.com";
    }
}

package server.app;

public class Constants {

  public static final class Mongo {
    public static final boolean AUTH_ENABLED = true;
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    public static final String DESCRIPTION = "mongodb-server-conn";
    public static final String DATABASE = "intelligeatsa";
    public static final String USER = "intelligeatsaUser";
    public static final char[] USER_PASS = "12345678".toCharArray();

    public static final int DEFAULT_LIMIT = 5;

    public static final String RECIPES_COLLECTION = "recipes";
    public static final String USERS_COLLECTION = "users";
  }

  public static final class Recipe {
    public static final String ID_TITLE = "_id";
    public static final String KEY_TITLE = "title";
    public static final String INDEX_TITLE_TEXT = "title_text";
  }

}

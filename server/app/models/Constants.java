package models;

public class Constants {

  public static final class Mongo {
    public static final boolean AUTH_ENABLED = true;
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    public static final String DESCRIPTION = "mongodb-server-conn";
    public static final String DATABASE = "test";
    public static final String USER = "intelligeatsaUser";
    public static final char[] USER_PASS = "12345678".toCharArray();

    public static final String RECIPES_COLLECTION = "recipes";
    public static final String USERS_COLLECTION = "users";
  }

}
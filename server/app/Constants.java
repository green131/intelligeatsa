package server.app;

public class Constants {

  public static final class Generic {
    public static final String ERROR = "error";
  }

  public static final class Mongo {
    public static final boolean AUTH_ENABLED = true;
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    public static final String DATABASE = "intelligeatsa";
    public static final String USER = "intelligeatsaUser";
    public static final char[] USER_PASS = "12345678".toCharArray();

    public static final int DEFAULT_LIMIT = 5;

    public static final String RECIPES_COLLECTION = "recipes";
    public static final String USERS_COLLECTION = "users";

    public static final String ID = "_id";
  }

  public static final class Recipe {
    public static final String INDEX_TITLE_TEXT = "title_text";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PICTUREURL = "pictureURL";
    public static final String KEY_SERVINGS = "servings";
    public static final String KEY_PREPTIME = "prepTime";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_PREPARATION = "preparation";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_NUMINGREDIENTS = "numOfIngredients";
    public static final String KEY_NUMPREPARATION = "numOfPreparationSteps";
  }

  public static final class User {
    public static final String KEY_USER = "user";
    public static final String KEY_PASS = "pass";
    public static final String KEY_TOKEN = "token";
  }
}
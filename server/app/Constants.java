package server.app;

public class Constants {

  public static final class Generic {
    public static final String ERROR = "error";
  }

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

    public static final String ID = "_id";
  }

  public static final class Sorting {
    public static final String KEY_SORT_METHOD = "sort";
    public static final String DEFAULT_SORT = "default";
    public static final String ALPHA_SORT = "alpha";
    public static final String ALPHA_SORT_R = "alphaR";
    public static final String RATING_SORT = "rating";
    public static final String RATING_SORT_R = "ratingR";
    public static final String PREP_SORT = "prep";
    public static final String PREP_SORT_R = "prepR";
  }

  public static final class Recipe {
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String INDEX_TITLE_TEXT = "title_text";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PICTUREURL = "pictureURL";
    public static final String KEY_SERVINGS = "servings";
    public static final String KEY_PREPTIME = "prepTime";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_PREPARATION = "preparation";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_INGREDIENTS_INDIVIDUAL = "ingredients.sectionIngredients.item";
    public static final String KEY_NUMINGREDIENTS = "numOfIngredients";
    public static final String KEY_NUMPREPARATION = "numOfPreparationSteps";

    public static final class Ingredients {
      public static final String FIELD_NAME = "ingredients";
      public static final String SECTION_INGREDIENTS = "sectionIngredients";
      public static final String ITEM = "item";
      public static final String QUANTITY = "quantity";
    }

    public static final class Rating {
      public static final String FIELD_NAME = "rating";
      public static final String VALUE = "value";
      public static final String NUM_OF_RATERS = "numOfRaters";
    }
  }

  public static final class User {
    public static final String ID_USER = "user";
    public static final String ID_PASS = "pass";
    public static final String ID_TOKEN = "token";
    public static final String ID_FB = "fbId";
    public static final String ID_GOOGLE = "googleId";

    public static final class GroceryList {
      public static final String FIELD_NAME = "groceryList";
      public static final String ID_RECIPE = "recipeID";
    }

    public static final class RatingList {
      public static final String FIELD_NAME = "ratingList";
      public static final String ID_RECIPE = "recipeID";
      public static final String MY_RATING = "myRating";
      public static final double UNINITIALIZED_RATING_VALUE = 0;
    }

    public static final class SavedList {
      public static final String FIELD_NAME = "savedList";
      public static final String ID_RECIPE = "recipeID";
    }
  }

  public static final class Routes {
    public static final String ID_USER = "userID";
    public static final String RECIPE_ID_LIST = "recipeIDList";
    public static final String INGREDIENTS = "ingredients";
  }

   public static final class Recommendation {
    public static final String RECIPES_FIELD_NAME = "recipes";
  }
}

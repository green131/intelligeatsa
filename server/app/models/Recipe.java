package server.app.models;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

public class Recipe extends BaseModelClass {

  private static final String KEY_TITLE = "title";
  private static final String INDEX_TITLE_TEXT = "title_text";

  public Recipe(Document doc) {
    super(Constants.Mongo.RECIPES_COLLECTION, doc);
  }

  /*
   * Returns a list of recipes that contain ALL of the specified tags.
   * i.e: if tags == Indian,Chicken, this function will return all
   * recipes that contain the tag "Indian", as well as the tag "Chicken"
   */
  public static ArrayList<Recipe> getRecipesByTag(MongoConnector conn, ArrayList<String> tags) {
    final ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    MongoCollection<Document> mongoCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);

    //create query
    ArrayList<Bson> filters = new ArrayList<Bson>();
    for (String tag : tags) {
      Bson filter = eq("tags", tag);
      filters.add(filter);
    }
    Bson finalQuery = and(filters);

    //get recipes
    FindIterable<Document> iter = mongoCollection.find(finalQuery);
    for (Document doc : iter) {
      Recipe recipe = new Recipe(doc);
      recipeList.add(recipe);
    }
    return recipeList;

  }

  /**
   * Sets up a full text search index
   * on the {@link Constants.Mongo#RECIPES_COLLECTION} if it
   * doesn't already exist
   */
  public static void setupSearchIndex(MongoConnector conn) {
    MongoCollection<Document> recipeCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    // try to find a text index
    ListIndexesIterable<Document> indexes = recipeCollection.listIndexes();
    boolean hasSearchIndex = false;
    for (Document index : indexes) {
      if (index.getString("name").equals(INDEX_TITLE_TEXT)) {
        hasSearchIndex = true;
        break;
      }
    }
    // create it if it doesn't exist
    if (!hasSearchIndex) {
      recipeCollection.createIndex(new Document(KEY_TITLE, "text"), new IndexOptions().name(INDEX_TITLE_TEXT));
    }
  }

}

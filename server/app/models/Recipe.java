package server.app.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import server.app.Constants;
import server.app.Utils;

import java.util.ArrayList;
import java.util.TreeMap;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;

public class Recipe extends BaseModelClass {

  public Recipe(Document doc) {
    super(Constants.Mongo.RECIPES_COLLECTION, doc);
  }

  /*
   * Returns a list of recipes that contain ALL of the specified tags.
   * i.e: if tags == Indian,Chicken, this function will return all
   * recipes that contain the tag "Indian", as well as the tag "Chicken"
   */
  public static ArrayList<Recipe> getRecipesByTag(MongoConnector conn, ArrayList<String> tags, int range_start, int range_end) {
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
    Utils.setupPaginator(iter, range_start, range_end);

    for (Document d : iter) {
      TreeMap<String, Object> keyMap = new TreeMap<String, Object>();
      keyMap.put("_id", new ObjectId(d.get("_id").toString()).toString());
      keyMap.put("title", d.get("title"));
      keyMap.put("description", d.get("description"));
      keyMap.put("pictureURL", d.get("pictureURL"));
      Document doc = new Document(keyMap);
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
      if (index.getString("name").equals(Constants.Recipe.INDEX_TITLE_TEXT)) {
        hasSearchIndex = true;
        break;
      }
    }
    // create it if it doesn't exist
    if (!hasSearchIndex) {
      recipeCollection.createIndex(new Document(Constants.Recipe.KEY_TITLE, "text"), new IndexOptions().name(Constants.Recipe.INDEX_TITLE_TEXT));
    }
  }

  public static ArrayList<Recipe> searchRecipesByTitle(MongoConnector conn, String recipe_title, int range_start, int range_end) {
    // Find all titles matching this title
    MongoCollection<Document> recipeCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    FindIterable<Document> searchResults = recipeCollection.find(text(recipe_title));
    ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

    Utils.setupPaginator(searchResults, range_start, range_end);

    for (Document doc : searchResults) {
      recipeList.add(new Recipe(doc));
    }

    return recipeList;
  }

  public static Recipe getRecipeById(MongoConnector conn, ObjectId id) {
    MongoCollection<Document> mongoCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);

    //create query
    Bson query = eq(Constants.Mongo.ID, id);

    //get recipes
    FindIterable<Document> iter = mongoCollection.find(query);
    Recipe r = new Recipe(iter.first());
    return r;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Recipe) {
      Recipe otherRecipe = (Recipe) other;
      return doc.equals(otherRecipe.doc);
    }
    return false;
  }
}

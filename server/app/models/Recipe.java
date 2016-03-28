package server.app.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Results;
import server.app.Constants;
import server.app.Global;
import server.app.Utils;
import server.app.exceptions.ServerResultException;

import java.util.ArrayList;
import java.util.TreeMap;

import static com.mongodb.client.model.Filters.*;

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
      Bson filter = eq(Constants.Recipe.KEY_TAGS, tag);
      filters.add(filter);
    }
    Bson finalQuery = and(filters);

    //get recipes
    FindIterable<Document> iter = mongoCollection.find(finalQuery);
    Utils.setupPaginator(iter, range_start, range_end);

    for (Document d : iter) {
      TreeMap<String, Object> keyMap = new TreeMap<String, Object>();
      keyMap.put(Constants.Mongo.ID, new ObjectId(d.get(Constants.Mongo.ID).toString()).toString());
      keyMap.put(Constants.Recipe.KEY_TITLE, d.get(Constants.Recipe.KEY_TITLE));
      keyMap.put(Constants.Recipe.KEY_DESCRIPTION, d.get(Constants.Recipe.KEY_DESCRIPTION));
      keyMap.put(Constants.Recipe.KEY_PICTUREURL, d.get(Constants.Recipe.KEY_PICTUREURL));
      Document doc = new Document(keyMap);
      Recipe recipe = new Recipe(doc);
      recipeList.add(recipe);
    }
    return recipeList;
  }


  public static ArrayList<String> find(MongoConnector conn, Bson query, int range_start, int range_end, String sortMode) {
    final ArrayList<String> recipeList = new ArrayList<String>();
    MongoCollection<Document> mongoCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);

    FindIterable<Document> iter = mongoCollection.find(query);//TODO sorting: .sort(new Document("title", -1));
    Utils.setupPaginator(iter, range_start, range_end);

    for (Document d : iter) {
      System.out.println(d.toJson());
      TreeMap<String, Object> keyMap = new TreeMap<String, Object>();
      keyMap.put(Constants.Mongo.ID, new ObjectId(d.get(Constants.Mongo.ID).toString()).toString());
      keyMap.put(Constants.Recipe.KEY_TITLE, d.get(Constants.Recipe.KEY_TITLE));
      keyMap.put(Constants.Recipe.KEY_DESCRIPTION, d.get(Constants.Recipe.KEY_DESCRIPTION));
      keyMap.put(Constants.Recipe.KEY_PICTUREURL, d.get(Constants.Recipe.KEY_PICTUREURL));
      Document doc = new Document(keyMap);
      //Recipe recipe = new Recipe(doc);
      recipeList.add(doc.toJson());
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


  public static Recipe getRecipeById(String recipeID) throws ServerResultException{

    //parse recipeID
    ObjectId rID = null;
    try {
      rID = new ObjectId(recipeID);
    } catch (IllegalArgumentException e) {
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed recipe id, not hexadecimal")));
    }

    //check if the given recipe and user exist
    Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, rID);
    if (recipe == null) {
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Could not find recipe matching id")));
    }
    
    return recipe;
    
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Recipe) {
      Recipe otherRecipe = (Recipe) other;
      return doc.equals(otherRecipe.doc);
    }
    return false;
  }

  @Override
  public String toString() {
    return doc.toString();
  }
}

package server.app.models;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;


public class Recipe extends BaseModelClass {

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

}

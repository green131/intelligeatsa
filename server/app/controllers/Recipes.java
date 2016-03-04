package server.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.models.Recipe;
import server.app.models.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;


//API for getting recipes
public class Recipes extends Controller {


  public static Result getRecipesByTag(String tags, int range_start, int range_end) {
    ObjectMapper mapper = new ObjectMapper();

    //extract tags from request
    ArrayList<String> tags_list = new ArrayList<String>(Arrays.asList(tags.split(",")));
    if (tags_list.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, "unable to parse tags"));
    }

    //get recipes
    ArrayList<Recipe> recipes = Recipe.getRecipesByTag(Global.mongoConnector, tags_list, range_start, range_end);
    if (recipes.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, "no recipes found"));
    }
    try {
      String json = mapper.writeValueAsString(recipes);
      JsonNode retNode = mapper.readTree(json);
      return ok(retNode);
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }



  public static Result getRecipesByTagDefault(String tags) {
    return getRecipesByTag(tags, 0, Constants.Mongo.DEFAULT_LIMIT);
  }



  public static Result searchRecipeTitles(String recipe_title, int range_start, int range_end) {
    ObjectMapper mapper = new ObjectMapper();

    if (range_end < range_start || range_end < 0 || range_start < 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, String.format("invalid recipe range: '%d' -> '%d'", range_start, range_end)));
    }
    ArrayList<Recipe> recipes = Recipe.searchRecipesByTitle(Global.mongoConnector, recipe_title, range_start, range_end);
    if (recipes.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, "no recipes found"));
    }
    try {
      String json = mapper.writeValueAsString(recipes);
      JsonNode retNode = mapper.readTree(json);
      return ok(retNode);
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }



  public static Result searchRecipeTitlesDefault(String recipe_title) {
    return searchRecipeTitles(recipe_title, 0, Constants.Mongo.DEFAULT_LIMIT);
  }



  public static Result getRecipeById(String id) {
    // parse object id
    ObjectId oid;
    try {
      oid = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "malformed recipe id, not hexadecimal"));
    }

    //get recipe
    Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, oid);
    if (recipe.doc == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "could not find recipe matching id"));
    }
    try {
      return ok(recipe.exportToString());
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }



  public static Result updateRating(String recipeID, double rating){

    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }
    
    //check if json request contains required information
    JsonNode userIDNode = requestJson.findPath("userID");
    if(!userIDNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }
    String userID = userIDNode.textValue();
    
    //parse recipeID and userID
    ObjectId rID, uID;
    try {
      rID = new ObjectId(recipeID);
    } catch (IllegalArgumentException e) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "malformed recipe id, not hexadecimal"));
    }
    try {
      uID = new ObjectId(userID);
    } catch (IllegalArgumentException e) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "malformed recipe id, not hexadecimal"));
    }

    //check if the given recipe and user exist
    Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, rID);
    if (recipe.doc == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "could not find recipe matching id"));
    }
    User user = User.getUserFromToken(Global.mongoConnector, uID);
    if(user == null){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "could not find user matching id"));    	
    }

    //perform the required database updates
    updateRecipeRating(rID, recipe, rating);  
    updateUsersListOfRatedRecipes(uID, user, rID, rating);
    return ok("Recipe rating updated!");
  }



  private static void updateRecipeRating(ObjectId recipeID, Recipe recipe, double rating){

    //get current rating
    Document recipeDoc = (Document)recipe.doc.get("rating");
    int newNumOfRaters = 1;
    double newRating = rating;

    //update rating if needed
    if(recipeDoc != null){
      double currentRating = recipeDoc.getDouble("value");
      int numOfRaters = recipeDoc.getInteger("numOfRaters");
      newNumOfRaters = numOfRaters + 1;
      newRating = ((currentRating*numOfRaters) + rating) / newNumOfRaters;
    }

    //update rating field in document
    Document ratingDoc = new Document();
    ratingDoc.append("value", newRating);
    ratingDoc.append("numOfRaters", newNumOfRaters);
    recipe.doc.append("rating", ratingDoc);

    //update database
    MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    Bson query = eq(Constants.Mongo.ID, recipeID);
    collection.replaceOne(query, recipe.doc);
  }



  private static void updateUsersListOfRatedRecipes(ObjectId userID, User user, ObjectId recipeID, double rating){

    //create a new list item for the given recipe rating
    Document ratingInfoDoc = new Document();
    ratingInfoDoc.append("recipeID", recipeID);
    ratingInfoDoc.append("myRating", rating);

    //check if this user already has a ratingList field
    ArrayList<Document> ratingListDoc = (ArrayList<Document>)user.doc.get("ratingList");
    if(ratingListDoc == null){
      ratingListDoc = new ArrayList<Document>();
    }

    //update ratingList field in document
    ratingListDoc.add(ratingInfoDoc);
    user.doc.append("ratingList", ratingListDoc);

    //update database
    MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
    Bson query = eq(Constants.Mongo.ID, userID);
    collection.replaceOne(query, user.doc);
  }
}

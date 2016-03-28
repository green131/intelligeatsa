package server.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.exceptions.ServerResultException;
import server.app.models.Recipe;
import server.app.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.*;


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


  /*
  # Search using multiple filters
  # INCLUDE AT LEAST ONE: {
    #    "title":       <keyword>,
    #    "tags":        <recipe_tags>,
    #    "ingredients": <recipe_ingredients>,
    #    "prepTime":    <prep_time>
    #    OPTIONAL:
    #      "sort":        <alpha|alphaR|rating|ratingR|prep|prepR|default>
  # }
  */
  public static Result searchRecipes(int range_start, int range_end) {
    ObjectMapper mapper = new ObjectMapper();

    if (range_end < range_start || range_end < 0 || range_start < 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, String.format("invalid recipe range: '%d' -> '%d'", range_start, range_end)));
    }

    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    //check if request is json array
    JsonNode titleNode = requestJson.findPath(Constants.Recipe.KEY_TITLE);
    JsonNode tagsNode = requestJson.findPath(Constants.Recipe.KEY_TAGS);
    JsonNode ingredientsNode = requestJson.findPath(Constants.Recipe.KEY_INGREDIENTS);
    JsonNode prepTimeNode = requestJson.findPath(Constants.Recipe.KEY_PREPTIME);
    JsonNode sortModeNode = requestJson.findPath(Constants.Sorting.KEY_SORT_METHOD);
    if(!titleNode.isTextual() && !tagsNode.isArray() && !ingredientsNode.isArray() && !prepTimeNode.isInt() &&!sortModeNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    // create query
    List<Bson> filters = new ArrayList<Bson>();
    // search title
    if (titleNode.size() > 0) filters.add(text(titleNode.textValue()));
    // search tags
    Iterator<JsonNode> tagsNodeIterator = tagsNode.elements();
    while (tagsNodeIterator.hasNext()) {
      JsonNode tagNode = tagsNodeIterator.next();
      if (tagNode.textValue().length() > 0) {
        Bson filter = eq(Constants.Recipe.KEY_TAGS, tagNode.textValue());
        filters.add(filter);
      }
    }
    // search ingredients
    Iterator<JsonNode> ingredientsNodeIterator = ingredientsNode.elements();
    List<Bson> filteri = new ArrayList<Bson>();
    while (ingredientsNodeIterator.hasNext()) {
      JsonNode ingredientNode = ingredientsNodeIterator.next();
      if (ingredientNode.textValue().length() > 0) {
        Bson filter = eq(Constants.Recipe.KEY_INGREDIENTS_INDIVIDUAL, ingredientNode.textValue());
        filteri.add(filter);
      }
    }
    if (filteri.size() > 0) filters.add(or(filteri));
    // search prepTime
    // Needs serious improvement. Should change / create a new value in the db for
    //  prepTimeMinutes to avoid string comparisons
    if (prepTimeNode.intValue() > 0) {
      String prepTime = String.valueOf(prepTimeNode.intValue()) + " minutes";
      Bson filtera = lte(Constants.Recipe.KEY_PREPTIME, prepTime);
      // I'm so sorry
      Bson filterb = regex(Constants.Recipe.KEY_PREPTIME, "^(?!.*hour(s)?$.*)\\d+\\sminute(s)?");
      Bson filterc = regex(Constants.Recipe.KEY_PREPTIME, "^(?!\\s*$).+");
      Bson filterd = exists(Constants.Recipe.KEY_PREPTIME);
      Bson filter = and(filtera, filterb, filterc, filterd);
      filters.add(filter);
    }

    // sort by requested settings
    String sortMode = Constants.Sorting.DEFAULT_SORT;
    if (sortModeNode.isTextual() && sortModeNode.textValue().length() > 0) {
      switch (sortModeNode.textValue()) {
        case Constants.Sorting.ALPHA_SORT:
        case Constants.Sorting.ALPHA_SORT_R:
        case Constants.Sorting.RATING_SORT:
        case Constants.Sorting.RATING_SORT_R:
        case Constants.Sorting.PREP_SORT:
        case Constants.Sorting.PREP_SORT_R:
          sortMode = sortModeNode.textValue();
          break;
        case Constants.Sorting.DEFAULT_SORT:
          break;
        default:
          return badRequest(new ObjectMapper().createObjectNode()
              .put(Constants.Generic.ERROR, "Malformed request: unknown sort type!"));
      }
    }
    Bson query = null;
    if (filters.size() > 0) query = and(filters);
    ArrayList<String> recipes = Recipe.find(Global.mongoConnector, query, range_start, range_end, sortMode);

    try {
      String json = Arrays.toString(recipes.toArray());
      JsonNode retNode = mapper.readTree(json);
      return ok(retNode);
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }


  public static Result searchRecipesDefault() {
    return searchRecipes(0, Constants.Mongo.DEFAULT_LIMIT);
  }


  public static Result updateRating(String recipeID, double rating){
    try{
      //get user and recipe
      User user = User.getUserFromJsonRequest(request());
      Recipe recipe = Recipe.getRecipeById(recipeID);
      ObjectId uID = user.getId();
      ObjectId rID = recipe.getId();
      
      //perform the required database updates
      updateRecipeRating(rID, recipe, rating);  
      updateUsersListOfRatedRecipes(uID, user, rID, rating);
      return ok("Recipe rating updated!");
    }
    catch(ServerResultException e){
      return e.errorResult;
    }
  }



  private static void updateRecipeRating(ObjectId recipeID, Recipe recipe, double rating){

    //get current rating
    Document recipeDoc = (Document)recipe.doc.get(Constants.Recipe.Rating.FIELD_NAME);
    int newNumOfRaters = 1;
    double newRating = rating;

    //update rating if needed
    if(recipeDoc != null){
      double currentRating = recipeDoc.getDouble(Constants.Recipe.Rating.VALUE);
      int numOfRaters = recipeDoc.getInteger(Constants.Recipe.Rating.NUM_OF_RATERS);
      newNumOfRaters = numOfRaters + 1;
      newRating = ((currentRating*numOfRaters) + rating) / newNumOfRaters;
    }

    //update rating field in document
    Document ratingDoc = new Document();
    ratingDoc.append(Constants.Recipe.Rating.VALUE, newRating);
    ratingDoc.append(Constants.Recipe.Rating.NUM_OF_RATERS, newNumOfRaters);
    recipe.doc.append(Constants.Recipe.Rating.FIELD_NAME, ratingDoc);

    //update database
    MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    Bson query = eq(Constants.Mongo.ID, recipeID);
    collection.replaceOne(query, recipe.doc);
  }



  private static void updateUsersListOfRatedRecipes(ObjectId userID, User user, ObjectId recipeID, double rating) throws ServerResultException{

    //create a new list item for the given recipe rating
    Document ratingInfoDoc = new Document();
    ratingInfoDoc.append(Constants.User.RatingList.ID_RECIPE, recipeID);
    ratingInfoDoc.append(Constants.User.RatingList.MY_RATING, rating);

    //check if this user already has a ratingList field
    ArrayList<Document> ratingListDoc;
    Object ratingListObj = user.doc.get(Constants.User.RatingList.FIELD_NAME);
    if(ratingListObj!=null &&  ratingListObj instanceof ArrayList){
      ratingListDoc = (ArrayList<Document>)ratingListObj;
    }
    else if(ratingListObj == null){
      ratingListDoc = new ArrayList<Document>();
    }
    else{
      throw new ServerResultException(internalServerError("Rating list in database not actually a list!"));
    }
    
    //update ratingList field in document
    ratingListDoc.add(ratingInfoDoc);
    user.doc.append(Constants.User.RatingList.FIELD_NAME, ratingListDoc);

    //update database
    MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
    Bson query = eq(Constants.Mongo.ID, userID);
    collection.replaceOne(query, user.doc);
  }
}

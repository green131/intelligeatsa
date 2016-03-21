package server.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;


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
    /*if (recipes.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put(Constants.Generic.ERROR, "no recipes found"));
    }*/
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
    #    "prep_time":   <prep_time>,
    #    "sort_alpha":  <true|false>
    #    "sort_rating": <true|false>
    # }
  */
  public static Result searchRecipes() {
    // TODO FIX THIS
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    //check if request is json array
    JsonNode titleNode = requestJson.findPath(Constants.Recipe.KEY_TITLE);
    JsonNode tagsNode = requestJson.findPath(Constants.Recipe.KEY_TAGS);
    JsonNode ingredientsNode = requestJson.findPath(Constants.Recipe.KEY_INGREDIENTS);
    JsonNode prepTimeNode = requestJson.findPath(Constants.Recipe.KEY_PREPTIME);
    JsonNode sortAlphaNode = requestJson.findPath(Constants.Sorting.KEY_ALPHASORT);
    JsonNode sortRatingNode = requestJson.findPath(Constants.Sorting.KEY_RATINGSORT);
    if(!titleNode.isTextual() && !tagsNode.isTextual() && !ingredientsNode.isArray() && !prepTimeNode.isInt()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "invalid credentials"));
  }

}

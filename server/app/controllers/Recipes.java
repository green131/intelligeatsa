package server.app.controllers;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import server.app.models.MongoConnector;
import server.app.models.Recipe;
import server.app.Constants;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.data.DynamicForm;
import play.data.Form;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;
import play.libs.Json.*;

import java.util.Arrays;


//API for getting recipes
public class Recipes extends Controller {

  public static Result getRecipesByTag(String tags, int range_start, int range_end) {
    MongoConnector mongoConnector = new MongoConnector();
    ObjectMapper mapper = new ObjectMapper();

    //extract tags from request
    ArrayList<String> tags_list = new ArrayList<String>(Arrays.asList(tags.split(",")));
    if (tags_list.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put("error", "unable to parse tags"));
    }

    //get recipes
    ArrayList<Recipe> recipes = Recipe.getRecipesByTag(mongoConnector, tags_list, range_start, range_end);
    if (recipes.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put("error", "no recipes found"));
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
    MongoConnector conn = new MongoConnector();
    ObjectMapper mapper = new ObjectMapper();

    if (range_end < range_start || range_end < 0 || range_start < 0) {
      return badRequest(mapper.createObjectNode()
          .put("error", String.format("invalid recipe range: '%d' -> '%d'", range_start, range_end)));
    }
    ArrayList<Recipe> recipes = Recipe.searchRecipesByTitle(conn, recipe_title, range_start, range_end);
    if (recipes.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put("error", "no recipes found"));
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
    MongoConnector mongoConnector = new MongoConnector();

    // parse object id
    ObjectId oid;
    try {
      oid = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put("error", "malformed recipe id, not hexadecimal"));
    }

    //get recipe
    Recipe recipe = Recipe.getRecipeById(mongoConnector, oid);
    if (recipe.doc == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put("error", "could not find recipe matching id"));
    }
    try {
      return ok(recipe.exportToString());
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }

}

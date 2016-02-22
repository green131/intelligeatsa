package server.app.controllers;

import static com.mongodb.client.model.Filters.text;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import server.app.models.MongoConnector;
import server.app.models.Recipe;
import server.app.models.Constants;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.data.DynamicForm;
import play.data.Form;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import play.libs.Json;
import play.libs.Json.*;
import java.util.Arrays;


//API for getting recipes
public class Recipes extends Controller {

  public static Result getRecipesByTag(String tags) {
    MongoConnector mongoConnector = new MongoConnector();
    ObjectMapper mapper = new ObjectMapper();

    //extract tags from request
    ArrayList<String> tags_list = new ArrayList<String>(Arrays.asList(tags.split(",")));
    if (tags_list.size() == 0) {
      return badRequest(mapper.createObjectNode()
          .put("error", "unable to parse tags"));
    }

    //get recipes
    ArrayList<Recipe> recipes = Recipe.getRecipesByTag(mongoConnector, tags_list);
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

  public static Result searchRecipeTitles(String recipe_title) {
    MongoConnector conn = new MongoConnector();
    ObjectMapper mapper = new ObjectMapper();

    // Find all titles matching this title
    MongoCollection<Document> recipeCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    FindIterable<Document> searchResults = recipeCollection.find(text(recipe_title));
    // TODO: pagination, more than 5 results?
    searchResults.limit(5);
    if (searchResults.first() == null) {
      return badRequest(mapper.createObjectNode()
          .put("error", "no recipes found"));
    }
    try {
      return ok(mapper.readTree(mapper.writeValueAsString(searchResults)));
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }

  public static Result getRecipeById(String id) {
    MongoConnector mongoConnector = new MongoConnector();

    //get recipe
    Recipe recipe = Recipe.getRecipeById(mongoConnector, id);
    if (recipe.doc == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put("error", "invalid recipe id"));
    }
    try {
      return ok(recipe.exportToString());
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }

}

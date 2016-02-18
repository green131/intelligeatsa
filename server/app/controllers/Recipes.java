package server.app.controllers;

import static com.mongodb.client.model.Filters.text;

import java.util.ArrayList;
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


//API for getting recipes
public class Recipes extends Controller {

  private static final String KEY_TITLE = "title";

  public static Result getRecipesByTag() {
    MongoConnector mongoConnector = new MongoConnector();

    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if (requestJson == null) {
      return badRequest("Expecting Json data!");
    }

    //check if request is json array
    JsonNode tagNode = requestJson.findPath("tags");
    if (!tagNode.isArray()) {
      return badRequest("Expecting Json array!");
    }

    //extract tags from request
    ArrayList<String> tags = new ArrayList<String>();
    for (JsonNode node : tagNode) {
      String tag = node.textValue();
      tag = tag.replaceAll("\\s+", "");
      tags.add(tag);
    }

    if (tags.size() == 0) {
      return badRequest("Invalid tags!");
    } else {
      //get recipes
      ArrayList<Recipe> recipes = Recipe.getRecipesByTag(mongoConnector, tags);
      ObjectMapper mapper = new ObjectMapper();
      try {
        String json = mapper.writeValueAsString(recipes);
        JsonNode retNode = mapper.readTree(json);
        return ok(retNode);
      } catch (Exception e) {
        e.printStackTrace();
        return internalServerError();
      }
    }
  }

  public static Result searchRecipeTitles() {
    MongoConnector conn = new MongoConnector();
    ObjectMapper mapper = new ObjectMapper();

    // Get request params as json
    JsonNode requestJson = request().body().asJson();
    if (requestJson == null) {
      return badRequest(mapper.createObjectNode()
                          .put("error", "bad request type"));
    }
    if (!requestJson.isObject()) {
      return badRequest(mapper.createObjectNode()
                          .put("error", "expecting json object"));
    }

    // Extract title from request params
    String title = null;
    if (requestJson.has(KEY_TITLE)) {
      JsonNode titleNode = requestJson.get(KEY_TITLE);
      if (titleNode.isTextual()) {
        title = titleNode.asText();
      }
    }
    if (title == null) {
      return badRequest(mapper.createObjectNode()
                          .put("error", "expecting title"));
    }

    // Find all titles matching this title
    MongoCollection<Document> recipeCollection = conn.getCollectionByName(Constants.Mongo.RECIPES_COLLECTION);
    FindIterable<Document> searchResults = recipeCollection.find(text(title));
    // TODO: pagination, more than 5 results?
    searchResults.limit(5);
    try {
      return ok(mapper.readTree(mapper.writeValueAsString(searchResults)));
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
    }
  }

}

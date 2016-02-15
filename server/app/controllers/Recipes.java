package server.app.controllers;

import java.util.ArrayList;

import server.app.models.MongoConnector;
import server.app.models.Recipe;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.data.DynamicForm;
import play.data.Form;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.libs.Json.*;


//API for getting recipes
public class Recipes extends Controller {

  private final static MongoConnector mongoConnector = new MongoConnector();

  public static Result getRecipesByTag() {

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
        System.out.println("Exception!");
        return ok("-1");
      }
    }

  }

}
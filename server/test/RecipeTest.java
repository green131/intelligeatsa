package server.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import play.mvc.Result;
import server.app.Global;
import server.app.controllers.Recipes;
import server.app.models.MongoConnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentAsString;


public class RecipeTest {

  public RecipeTest() {
    Global.mongoConnector = new MongoConnector();
  }

  @Test
  public void testGetRecipeById_found() {
    Result res = Recipes.getRecipeById("56ba001c82064421f55cf903");
    assertEquals(res.status(), 200);
  }

  @Test
  public void testGetRecipeById_notFound() {
    Result res = Recipes.getRecipeById("000000000000000000000000");
    assertEquals(res.status(), 400);
  }

  @Test
  public void testGetRecipeById_invalidHexadecimal() {
    Result res = Recipes.getRecipeById("abc");
    assertEquals(res.status(), 400);
  }

  @Test
  public void testGetRecipeByTags_notfound() {
    String tags = "ayy lmao";
    Result res = Recipes.getRecipesByTagDefault(tags);
    assertEquals(400, res.status());
  }

  @Test
  public void testGetRecipeByTags_Found() {
    String tags = "Mango";
    Result res = Recipes.getRecipesByTagDefault(tags);
    assertEquals(200, res.status());
    assertTrue(contentAsString(res).contains("Mango"));
  }

  @Test
  public void testGetRecipeFullText() {
    String title = "Mango";
    Result res = Recipes.searchRecipeTitlesDefault(title);
    assertEquals(200, res.status());

    try {
      //check that all recipe titles contain mango
      ObjectMapper mapper = new ObjectMapper();
      JsonNode resNode = mapper.readTree(contentAsString(res));
      for (JsonNode recipeNode : resNode) {
        JsonNode docNode = recipeNode.get("doc");
        String recipeTitle = docNode.get("title").asText();
        assertTrue(recipeTitle.contains("Mango"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
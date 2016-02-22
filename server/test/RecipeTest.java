package server.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import play.mvc.Result;
import server.app.controllers.Recipes;
import server.app.models.MongoConnector;
import server.app.models.Recipe;

public class RecipeTest {

	MongoConnector mongoConnector = new MongoConnector();
	
  @Test
  public void testGetRecipeById_found() {
    Recipe recipe = Recipe.getRecipeById(mongoConnector, "56ba001c82064421f55cf903");
    System.out.println(recipe.toString());
  }
  
  @Test
  public void testGetRecipeById_notFound() {
  	Recipe recipe = Recipe.getRecipeById(mongoConnector, "56ba");
    System.out.println(recipe.toString());
  }
  
  @Test
  public void testGetRecipeById_invalidHexadecimal() {
  	Recipe recipe = Recipe.getRecipeById(mongoConnector, "abc");
    System.out.println(recipe.toString());
  }
    
  @Test
  public void testGetRecipeByTags_notfound() {
  	ArrayList<String> tags = new ArrayList<String>();
  	tags.add("yolo");
  	ArrayList<Recipe> list = Recipe.getRecipesByTag(mongoConnector, tags);
    System.out.println(list.toString());
  }
  
  
  @Test
  public void testGetRecipeByTags_Found() {
  	ArrayList<String> tags = new ArrayList<String>();
  	tags.add("Indian");
    ArrayList<Recipe> list = Recipe.getRecipesByTag(mongoConnector, tags);
    System.out.println(list.toString());
  }
  
  @Test
  public void testGetRecipeFullText() {
  	String title = "Mango";
  	ArrayList<String> tags = new ArrayList<String>();
  	tags.add("Indian");
    ArrayList<Recipe> list = Recipe.getRecipesByTag(mongoConnector, tags);
    System.out.println(list.toString());
  }

}
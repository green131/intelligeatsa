package server.app.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import server.app.models.MongoConnector;
import server.app.models.Recipe;
import server.app.models.Utils;
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
		
		//get POST parameter data
		DynamicForm dynamicForm = play.data.Form.form().bindFromRequest();
		String tagData = dynamicForm.get("tags");
		tagData.replaceAll("\\s+","");
		String tags[] = tagData.split(",");
		
		if(tags.length == 0){
			System.out.println("No tags specified!");
			return ok("-1");
		}
		else{
			
			//get recipes
			ArrayList<Recipe> recipes = Recipe.getRecipesByTag(mongoConnector, tags);
			ObjectMapper mapper = new ObjectMapper();
			try{
				String json = mapper.writeValueAsString(recipes);
				JsonNode retNode = mapper.readTree(json);
				return ok(retNode);
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("Exception!");
				return ok("-1");
			}
		}
	}

}
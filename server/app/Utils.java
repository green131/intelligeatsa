package server.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;

import org.bson.Document;
import org.bson.types.ObjectId;

import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import server.app.models.Recipe;
import server.app.models.RecipeUserWrapper;
import server.app.models.User;

import java.util.Collection;


//extends Controller because it needs to use some protected methods
public class Utils extends Controller{

  public static void setupPaginator(FindIterable<Document> iter, int range_start, int range_end) {
    // get results in range
    if (range_start == range_end) {
      iter.skip(range_start);
      iter.limit(1);
    } else if (range_start == 0) {
      iter.limit(range_end);
    } else {
      iter.skip(range_start);
      iter.limit(range_end - 1);
    }
  }

  public static int sizeof(Iterable<?> it) {
    if (it instanceof Collection)
      return ((Collection<?>)it).size();
    // else iterate
    int i = 0;
    for (Object obj : it) i++;
    return i;
  }


  //if returnObject.serverErrorResult == null, everything was successfully read
  public static RecipeUserWrapper getRecipeAndUserFromRequest(Request request, String recipeID){
    
    //check if request is json
    RecipeUserWrapper returnObject = new RecipeUserWrapper();
    JsonNode requestJson = request.body().asJson();
    if(requestJson == null) {
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Expecting Json data!"));
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    
    //check if json request contains required information
    JsonNode userIDNode = requestJson.findPath(Constants.Routes.ID_USER);
    if(!userIDNode.isTextual()){
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    
    //parse recipeID and userID
    String userID = userIDNode.textValue();
    ObjectId rID, uID;
    try {
      rID = new ObjectId(recipeID);
    } catch (IllegalArgumentException e) {
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed recipe id, not hexadecimal"));
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    try {
      uID = new ObjectId(userID);
    } catch (IllegalArgumentException e) {
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed user id, not hexadecimal"));
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    
    //check if the given recipe and user exist
    Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, rID);
    if (recipe.doc == null) {
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Could not find recipe matching id"));
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    User user = User.getUserFromToken(Global.mongoConnector, uID);
    if(user == null){
      Result result = badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Could not find user matching id"));      
      returnObject.serverErrorResult = result;
      return returnObject;
    }
    
    //if we reached over here, there were no errors ==> return the required information
    returnObject.userID = uID;
    returnObject.recipeID = rID;
    returnObject.user = user;
    returnObject.recipe = recipe;
    return returnObject;
    
  }
}

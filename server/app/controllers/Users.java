package server.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.models.Recipe;
import server.app.models.RecipeUserWrapper;
import server.app.models.User;
import server.app.models.Ingredient;
import server.app.Utils;

import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.eq;

//API for user information
public class Users extends Controller {

  public static Result loginUser() {
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    //check if request is json array
    JsonNode userNode = requestJson.findPath(Constants.User.ID_USER);
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);
    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    String username = userNode.textValue();
    String password = passNode.textValue();

    User u = new User(username);
    if (u.isAuthValid(password)) {
      return ok(new ObjectMapper().createObjectNode()
          .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString())
          .put(Constants.User.ID_USER, u.doc.getString(Constants.User.ID_USER)));
    }
    return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "invalid credentials"));
  }



  public static Result registerUser() {
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    //check if request is json array
    JsonNode userNode = requestJson.findPath(Constants.User.ID_USER);
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);

    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    String username = userNode.textValue();
    String password = passNode.textValue();

    if (User.usernameExists(username)) {
      return badRequest("Username already exists!");
    } else if (password.length() < 5) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Password needs to be 5 characters or more!"));
    }

    // create new user doc and store in db
    User u = new User(username);
    u.doc = new Document();
    u.addAttribute(Constants.User.ID_USER, username);
    u.addAttribute(Constants.User.ID_PASS, password);
    Global.mongoConnector.saveDocument(u.collection, u.doc);

    // check doc was saved correctly and return token
    if (u.isAuthValid(password)) {
      return ok(new ObjectMapper().createObjectNode()
          .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString())
          .put(Constants.User.ID_USER, u.doc.getString(Constants.User.ID_USER)));
    }
    return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "could not save new user account"));
  }



  public static Result addRecipeToGroceryList(String recipeID){

    RecipeUserWrapper recipeUserWrapper = Utils.getRecipeAndUserFromRequest(request(), recipeID);
    if(recipeUserWrapper.serverErrorResult != null){
      //error occured while processing the request
      return recipeUserWrapper.serverErrorResult;
    }
    else{

      //check if this user has a groceryList field
      ArrayList<Document> groceryListDoc = (ArrayList<Document>)recipeUserWrapper.user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      if(groceryListDoc == null){
        groceryListDoc = new ArrayList<Document>();
      }

      if(groceryListContains(groceryListDoc, recipeUserWrapper.recipeID)){
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "User's grocery list already contains the given recipeID"));  
      }
      else{
        //create new groceryList element
        Document groceryListElementDoc = new Document();
        groceryListElementDoc.append(Constants.User.GroceryList.ID_RECIPE, recipeUserWrapper.recipeID);

        //update groceryList field in document
        groceryListDoc.add(groceryListElementDoc);
        recipeUserWrapper.user.doc.append(Constants.User.GroceryList.FIELD_NAME, groceryListDoc);

        //update database
        MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
        Bson query = eq(Constants.Mongo.ID, recipeUserWrapper.userID);
        collection.replaceOne(query, recipeUserWrapper.user.doc);
        return ok("Grocery list updated!");
      }

    }

  }



  public static Result removeRecipeFromGroceryList(String recipeID){
    
    RecipeUserWrapper recipeUserWrapper = Utils.getRecipeAndUserFromRequest(request(), recipeID);
    if(recipeUserWrapper.serverErrorResult != null){
      //error occured while processing the request
      return recipeUserWrapper.serverErrorResult;
    }
    else{

      //check if this user has a groceryList field
      ArrayList<Document> groceryListDoc = (ArrayList<Document>)recipeUserWrapper.user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      if(groceryListDoc == null){
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "User's grocery list does not contain the given recipeID"));  
      }

      if(groceryListContains(groceryListDoc, recipeUserWrapper.recipeID) == false){
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "User's grocery list does not contain the given recipeID"));  
      }
      else{

        //update groceryList field in document
        Document recipeIDToRemove = null;
        for(Document doc : groceryListDoc){
          if(doc.get(Constants.User.GroceryList.ID_RECIPE).equals(recipeUserWrapper.recipeID)){
            recipeIDToRemove = doc;
            break;
          }
        }
        groceryListDoc.remove(recipeIDToRemove);
        recipeUserWrapper.user.doc.append(Constants.User.GroceryList.FIELD_NAME, groceryListDoc);

        //update database
        MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
        Bson query = eq(Constants.Mongo.ID, recipeUserWrapper.userID);
        collection.replaceOne(query, recipeUserWrapper.user.doc);
        return ok("Grocery list updated!");
      }

    }
  }



  public static Result getGroceryList(){
    
    RecipeUserWrapper recipeUserWrapper = Utils.getUserFromJsonRequest(request());
    if(recipeUserWrapper.serverErrorResult != null){
      //error occured while processing the request
      return recipeUserWrapper.serverErrorResult;
    }
    else{
      
      //add ingredients from each recipe into the cumulative list
      ArrayList<Ingredient> cumulativeIngredientList = new ArrayList<Ingredient>();
      ArrayList<Document> groceryListDoc = (ArrayList<Document>)recipeUserWrapper.user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      
      if(groceryListDoc != null){
        for(Document doc : groceryListDoc){
          ObjectId recipeID = (ObjectId)doc.get(Constants.User.GroceryList.ID_RECIPE);
          Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, recipeID);
          List<Ingredient> ingredientsInCurrentRecipe = getIngredientsInRecipe(recipe);
          cumulativeIngredientList.addAll(ingredientsInCurrentRecipe);
        }
      }

      //create JsonNode and return
      try {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cumulativeIngredientList);
        JsonNode retNode = mapper.readTree(json);
        return ok(retNode);
      } catch (Exception e) {
        e.printStackTrace();
        return internalServerError();
      }
    }
  }



  private static boolean groceryListContains(ArrayList<Document> groceryList, ObjectId recipeID){
    for(Document doc : groceryList){
      if(doc.get(Constants.User.GroceryList.ID_RECIPE).equals(recipeID)){
        return true;
      }
    }
    return false;
  }
  
  
  
  private static List<Ingredient> getIngredientsInRecipe(Recipe recipe){
    ArrayList<Ingredient> retList = new ArrayList<Ingredient>();
    ArrayList<Document> allIngredientsDoc = (ArrayList<Document>) recipe.doc.get(Constants.Recipe.Ingredients.FIELD_NAME);
    
    for(Document doc : allIngredientsDoc){
      ArrayList<Document> ingredientsInThisSectionDoc = (ArrayList<Document>) doc.get(Constants.Recipe.Ingredients.SECTION_INGREDIENTS);
      for(Document ingredientDoc : ingredientsInThisSectionDoc){
        String item = (String) ingredientDoc.get(Constants.Recipe.Ingredients.ITEM);
        String quantity = (String) ingredientDoc.get(Constants.Recipe.Ingredients.QUANTITY);
        Ingredient ingredient = new Ingredient(quantity, item);
        retList.add(ingredient);
      }
    }
    return retList;
  }

}

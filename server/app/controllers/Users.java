package server.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.exceptions.ServerResultException;
import server.app.models.Recipe;
import server.app.models.User;
import server.app.models.Ingredient;
import server.app.Utils;

import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.eq;

//API for user information
public class Users extends Controller {
  public static Result loginUser() {
    ObjectMapper objectMapper = new ObjectMapper();
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(objectMapper.createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }
    
    // check for social login
    String socialIdType = null;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(objectMapper.createObjectNode()
      .put(Constants.Generic.ERROR, "Can only specify 1 social id!"));
    } else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdType = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdType = Constants.User.ID_GOOGLE;
    }
    if (socialIdType != null) {
      User user = User.getUserFromSocialId(Global.mongoConnector, socialIdType, requestJson.get(socialIdType).asText());
      if (user != null) {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode ratingsArray = getUserRatings(user);
        result.put(Constants.User.ID_TOKEN, user.generateUserToken().toHexString());
        result.put(Constants.User.ID_USER, user.doc.getString(Constants.User.ID_USER));
        result.put(Constants.User.RatingList.FIELD_NAME, ratingsArray);
        return ok(result);
      } else {
        return badRequest(objectMapper.createObjectNode()
        .put(Constants.Generic.ERROR, "bad social id"));
      }
    }
    
    //check if request is json array
    JsonNode userNode = requestJson.findPath(Constants.User.ID_USER);
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);
    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest(objectMapper.createObjectNode()
      .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }
    
    String username = userNode.textValue();
    String password = passNode.textValue();
    
    User u = new User(username);
    if (u.isAuthValid(password)) {
      ArrayNode ratingsArray = getUserRatings(u);
      ObjectNode result = objectMapper.createObjectNode();
      result.put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString());
      result.put(Constants.User.ID_USER, u.doc.getString(Constants.User.ID_USER));
      if(ratingsArray != null){
        result.put(Constants.User.RatingList.FIELD_NAME, ratingsArray);
      }
      return ok(result);
    }
    return badRequest(objectMapper.createObjectNode()
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
    if (!userNode.isTextual()) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting text user name"));
    }
    
    String socialIdType = null;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Can only specify 1 social id!"));
    } else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdType = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdType = Constants.User.ID_GOOGLE;
    }
    if (socialIdType != null) {
      String socialId = requestJson.get(socialIdType).asText();
      if (User.getUserFromSocialId(Global.mongoConnector, socialIdType, socialId) != null) {
        return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "social id already taken"));
      }
      
      User u = new User(userNode.asText());
      u.doc = new Document();
      u.addAttribute(Constants.User.ID_USER, userNode.asText());
      u.addAttribute(socialIdType, socialId);
      u.persist(Global.mongoConnector);
      return ok(new ObjectMapper().createObjectNode()
      .put(Constants.User.ID_USER, userNode.asText())
      .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString()));
    }
    // duplicate email check
    if (User.usernameExists(userNode.asText())) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Username already taken"));
    }
    
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);
    if(!passNode.isTextual()) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }
    
    String username = userNode.textValue();
    String password = passNode.textValue();
    if (password.length() < 5) {
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
  
  public static Result linkUserAccount() {
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }
    
    User user = User.getUserFromRequest(Global.mongoConnector, requestJson);
    if (user == null) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting user info!"));
    }
    
    String socialIdType;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting either fb user id or google user id"));
    }
    else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdType = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdType = Constants.User.ID_GOOGLE;
    } else {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Expecting either fb user id or google user id"));
    }
    String socialId = requestJson.get(socialIdType).asText();
    User existingUser = User.getUserFromSocialId(Global.mongoConnector, socialIdType, socialId);
    if (existingUser != null) {
      return badRequest(new ObjectMapper().createObjectNode()
      .put(Constants.Generic.ERROR, "Social id already taken"));
    }
    user.changeAttribute(socialIdType, socialId);
    user.persist(Global.mongoConnector);
    return ok();
  }
  
  public static ArrayNode getUserRatings(User user){
    ObjectMapper objectMapper = new ObjectMapper();
    if(user != null){
      ArrayList<Document> ratings = (ArrayList<Document>) user.doc.get(Constants.User.RatingList.FIELD_NAME);
      ArrayNode ratingsArray = objectMapper.createArrayNode();
      if (ratings != null) {
        for (Document doc : ratings) {
          ObjectNode ratingsObject = objectMapper.createObjectNode();
          ObjectId recipeIDObj = (ObjectId)doc.get(Constants.User.RatingList.ID_RECIPE);
          ratingsObject.put(Constants.User.RatingList.ID_RECIPE, recipeIDObj.toHexString());
          ratingsObject.put(Constants.User.RatingList.MY_RATING, doc.getDouble(Constants.User.RatingList.MY_RATING));
          ratingsArray.add(ratingsObject);
        }
      }
      // will be an empty array if no ratings for the user.
      return ratingsArray;
    }else{
      return null;
    }
  }
  
  
  public static Result addRecipeToGroceryList(String recipeID){
    
    try{
      //get user and recipe
      User user = User.getUserFromJsonRequest(request());
      Recipe recipe = Recipe.getRecipeById(recipeID);
      ObjectId uID = user.getId();
      ObjectId rID = recipe.getId();
      
      //check if this user has a groceryList field
      ArrayList<Document> groceryListDoc;
      Object groceryListObj = user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      if(groceryListObj!=null &&  groceryListObj instanceof ArrayList){
        groceryListDoc = (ArrayList<Document>)groceryListObj;
      }
      else if(groceryListObj == null){
        groceryListDoc = new ArrayList<Document>();
      }
      else{
        return internalServerError("Grocery list in database not actually a list!");
      }
      
      
      if(groceryListContains(groceryListDoc, rID)){
        return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "User's grocery list already contains the given recipeID"));
      }
      else{
        //create new groceryList element
        Document groceryListElementDoc = new Document();
        groceryListElementDoc.append(Constants.User.GroceryList.ID_RECIPE, rID);
        
        //update groceryList field in document
        groceryListDoc.add(groceryListElementDoc);
        user.doc.append(Constants.User.GroceryList.FIELD_NAME, groceryListDoc);
        
        //update database
        MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
        Bson query = eq(Constants.Mongo.ID, uID);
        collection.replaceOne(query, user.doc);
        return ok("Grocery list updated!");
      }
      
    }
    catch(ServerResultException e){
      return e.errorResult;
    }
    
  }
  
  
  
  public static Result removeRecipeFromGroceryList(String recipeID){
    
    try{
      //get user and recipe
      User user = User.getUserFromJsonRequest(request());
      Recipe recipe = Recipe.getRecipeById(recipeID);
      ObjectId uID = user.getId();
      ObjectId rID = recipe.getId();
      
      //check if this user has a groceryList field
      ArrayList<Document> groceryListDoc;
      Object groceryListObj = user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      if(groceryListObj!=null &&  groceryListObj instanceof ArrayList){
        groceryListDoc = (ArrayList<Document>)groceryListObj;
      }
      else if(groceryListObj == null){
        return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "User's grocery list does not contain the given recipeID"));
      }
      else{
        return internalServerError("Grocery list in database not actually a list!");
      }
      
      
      if(groceryListContains(groceryListDoc, rID) == false){
        return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "User's grocery list does not contain the given recipeID"));
      }
      else{
        //update groceryList field in document
        Document recipeIDToRemove = null;
        for(Document doc : groceryListDoc){
          if(doc.get(Constants.User.GroceryList.ID_RECIPE).equals(rID)){
            recipeIDToRemove = doc;
            break;
          }
        }
        groceryListDoc.remove(recipeIDToRemove);
        user.doc.append(Constants.User.GroceryList.FIELD_NAME, groceryListDoc);
        
        //update database
        MongoCollection<Document> collection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);
        Bson query = eq(Constants.Mongo.ID, uID);
        collection.replaceOne(query, user.doc);
        return ok("Grocery list updated!");
      }
      
    }
    catch(ServerResultException e){
      return e.errorResult;
    }
  }
  
  
  
  public static Result getGroceryList(){
    
    ArrayList<Document> recipeIdList = new ArrayList<Document>();
    ArrayList<Ingredient> cumulativeIngredientList = new ArrayList<Ingredient>();
    try{
      //get user
      User user = User.getUserFromJsonRequest(request());
      
      //check if this user has a groceryList field
      Object groceryListObj = user.doc.get(Constants.User.GroceryList.FIELD_NAME);
      if(groceryListObj != null){
        if( !(groceryListObj instanceof ArrayList) ){
          return internalServerError("Grocery list in database not actually a list!");
        }
        else{
          
          //add ingredients from each recipe into the cumulative list
          ArrayList<Document> groceryListDoc = (ArrayList<Document>)groceryListObj;
          for(Document doc : groceryListDoc){
            Object idObj = doc.get(Constants.User.GroceryList.ID_RECIPE);
            
            if(idObj instanceof ObjectId){
              ObjectId recipeID = (ObjectId)idObj;
              Recipe recipe = Recipe.getRecipeById(Global.mongoConnector, recipeID);
              List<Ingredient> ingredientsInCurrentRecipe = getIngredientsInRecipe(recipe);
              recipeIdList.add(new Document().append(Constants.Recipe.KEY_ID,recipeID.toHexString()).append(Constants.Recipe.KEY_TITLE, recipe.getAttribute(Constants.Recipe.KEY_TITLE)));
              cumulativeIngredientList.addAll(ingredientsInCurrentRecipe);
            }
            else{
              return internalServerError("Recipe ID not stored as ObjectId in database!");
            }
            
          }
        }
      }
      
    }
    catch(ServerResultException e){
      return e.errorResult;
    }
    
    //create ObjectNode and return
    try {
      ObjectMapper mapper = new ObjectMapper();
      String recipeIDListJson = mapper.writeValueAsString(recipeIdList);
      String ingredientsJson = mapper.writeValueAsString(cumulativeIngredientList);
      
      JsonNode recipeIDListNode = mapper.readTree(recipeIDListJson);
      JsonNode ingredientsNode = mapper.readTree(ingredientsJson);
      
      ObjectNode retNode = mapper.createObjectNode();
      retNode.put(Constants.Routes.RECIPE_ID_LIST, recipeIDListNode);
      retNode.put(Constants.Routes.INGREDIENTS, ingredientsNode);
      return ok(retNode);
      
    } catch (Exception e) {
      e.printStackTrace();
      return internalServerError();
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

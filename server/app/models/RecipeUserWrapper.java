package server.app.models;

import org.bson.types.ObjectId;

import play.mvc.Result;


//A simple wrapper class used by server.app.Utils.java
public class RecipeUserWrapper {
  
  public Result serverErrorResult;
  public ObjectId userID, recipeID;
  public User user;
  public Recipe recipe;
  
  public RecipeUserWrapper(){
    this.serverErrorResult = null;
    this.userID = null;
    this.recipeID = null;
    this.user = null;
    this.recipe = null;
  }
  
  public RecipeUserWrapper(Result serverResult, ObjectId userID, ObjectId recipeID, User user, Recipe recipe){
    this.serverErrorResult = serverResult;
    this.userID = userID;
    this.recipeID = recipeID;
    this.user = user;
    this.recipe = recipe;
  }
  
}

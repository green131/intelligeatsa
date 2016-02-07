package models;

import org.jongo.MongoCollection;

import models.Constants.*;


public class Recipe extends BaseModelClass{

  MongoCollection recipes;

  public Recipe() {
    recipes = this.jongo.getCollection(DB_RECIPE_NAME);
  }

  @MongoObjectId
  public String _id;

  public String name;

  public String cuisineType;

  public ArrayList<String> ingredients;

  public Recipe FindRecipeById(int id) {
    return recipes.findOne(("{_id: '%d'}"), id).as(Recipe.class);
  }

}
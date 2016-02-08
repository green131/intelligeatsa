package main.java;

import java.util.ArrayList;

public class Recipe {
    String title, description, pictureURL, servings, prepTime;
    ArrayList<String> tags;
    ArrayList<ArrayList<String>> preparation;
    ArrayList<IngredientSection> ingredients;
    NutritionInformation nutrition;
    int numOfIngredients, numOfPreparationSteps;

    public Recipe(String title, String description, String pictureURL, String servings,
                  String prepTime, ArrayList<String> tags, ArrayList<ArrayList<String>> preparation,
                  ArrayList<IngredientSection> ingredients, NutritionInformation nutrition,
                  int numOfIngredients, int numOfPreparationSteps) {
        this.title = title;
        this.description = description;
        this.pictureURL = pictureURL;
        this.servings = servings;
        this.prepTime = prepTime;
        this.tags = tags;
        this.preparation = preparation;
        this.ingredients = ingredients;
        this.nutrition = nutrition;
        this.numOfIngredients = numOfIngredients;
        this.numOfPreparationSteps = numOfPreparationSteps;
    }
}

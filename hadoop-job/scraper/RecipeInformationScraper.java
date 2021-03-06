package scraper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;


public class RecipeInformationScraper {
	
	private Subscriber<org.bson.Document> recipeSubscriber;
	private static final Gson gson = new Gson();
	
	public RecipeInformationScraper(Subscriber<org.bson.Document> recipeSubscriber){
		this.recipeSubscriber = recipeSubscriber;
	}
	
	public void scrapeRecipeInformation(ArrayList<String> recipeURLs){
		for(String recipeURL : recipeURLs){
			scrapeRecipeInformation(recipeURL);
		}
	}
	
	public void scrapeRecipeInformation(String recipeURL){
		recipeURL = "http://cooking.nytimes.com" + recipeURL;
		Document doc;
		try {
			doc = Jsoup.connect(recipeURL).get();
			
			String title = doc.select("h1[class=recipe-title title name]").first().text().trim();
			
			//get description
			String description;
			try{
				description = doc.select("div[class=topnote]").select("p").first().text().trim();
			}
			catch(NullPointerException e){
				description = "";
			}
			
			
			//get pictureURL
			String pictureURL;
			try{
				pictureURL = doc.select("div[class=media-container]").select("img").first().attr("src").trim();
			}
			catch(NullPointerException e){
				pictureURL = "";
			}
			
			String prepTime = doc.select("ul[class=recipe-time-yield]").select("li").first().text().substring(4).trim();
			String servings = doc.select("ul[class=recipe-time-yield]").select("li").get(1).text().substring(5).trim();
			
			
			//get tags
			ArrayList<String> tags = new ArrayList<String>();
			Elements tagElements = doc.select("p[class=special-diets tag-block]").select("a");
			for(Element tagElement : tagElements){
				tags.add(tagElement.text().trim());
			}

			
			//get preparation
			//dividing preparation steps into groups if needed (i.e: make the bread, make the chicken)
			ArrayList<ArrayList<String>> preparation = new ArrayList<ArrayList<String>>();
			Elements prepHeaders = doc.select("h4[class=group-name]");
			Elements prepLists = doc.select("ol[class=recipe-steps]");
			int numOfPreparationSteps = 0;
			for(int i=0; i<prepLists.size(); i++){
				ArrayList<String> preparationForThisSubsection = new ArrayList<String>();
				
				//only add section header if there are multiple section headers
				if(prepHeaders.size()>i && prepLists.size()>1){
					preparationForThisSubsection.add(prepHeaders.get(i).text().trim());
				}
				Elements prepSteps = prepLists.get(i).select("li");
				for(Element prepStep : prepSteps){
					numOfPreparationSteps++;
					preparationForThisSubsection.add(prepStep.text().trim());
				}
				preparation.add(preparationForThisSubsection);
			}
			
			
			//get ingredients
			//dividing ingredients into groups if needed
			ArrayList<IngredientSection> ingredients = new ArrayList<IngredientSection>();
			Elements ingredientHeaders = doc.select("section[class=recipe-ingredients-wrap]").select("h4[class=part-name]");
			Elements ingredientLists = doc.select("section[class=recipe-ingredients-wrap]").select("ul[class=recipe-ingredients]");
			int numOfIngredients = 0;
			for(int i=0; i<ingredientLists.size()-1; i++){
				
				String sectionName = "";
				//only add section header if there are multiple section headers
				if(ingredientHeaders.size()>i && ingredientLists.size()>1){
					sectionName = ingredientHeaders.get(i).text().trim();
				}
				
				ArrayList<Ingredient> sectionIngredients = new ArrayList<Ingredient>();
				Elements ingredientList = ingredientLists.get(i).select("li");
				for(Element ingredient : ingredientList){
					numOfIngredients++;
					String quantity = ingredient.select("span[class=quantity]").text().trim();
					String itemDetails = ingredient.select("span[class=ingredient-name]").html();
					
					/*
					 * HTML format: amount <span>ingredientName</span> optionalInstructions
					 * 
					 * Note: amount and/or optionalInstructions may not be available
					 */
					int indexOfFirstSpanTag = itemDetails.indexOf("<span>");
					String arr[] = itemDetails.split("<");
					String amount = "";
					String item = "";
					String optionalInformation = "";
					
					if(arr.length == 1){
						item = arr[0].substring(5);
					}
					else if(arr.length == 2){
						if(indexOfFirstSpanTag == 0){
							item = arr[0].substring(5);
							optionalInformation = arr[1].substring(6);
						}
						else{
							amount = arr[0];
							item = arr[1].substring(5);
						}
					}
					else if(arr.length == 3){
						amount = arr[0];
						item = arr[1].substring(5);
						optionalInformation = arr[2].substring(6);
					}

					
					if(!quantity.equals("")){
						quantity = quantity + " " + amount;
					}
					else{
						item = amount + " " + item;
					}
					
					Ingredient in = new Ingredient(quantity.trim(), item.trim(), optionalInformation.trim());
					sectionIngredients.add(in);
				}
				IngredientSection ingredientSection = new IngredientSection(sectionName, sectionIngredients);
				ingredients.add(ingredientSection);
			}
			
			
			//get nutritional information
			NutritionInformation nutritionInformation = new NutritionInformation();
			nutritionInformation.calories = doc.select("span[itemprop=calories]").text().trim();
			nutritionInformation.fat = doc.select("span[itemprop=fatContent]").text().trim();
			nutritionInformation.saturatedFat = doc.select("span[itemprop=saturatedFatContent]").text().trim();
			nutritionInformation.transFat = doc.select("span[itemprop=transFatContent]").text().trim();
			nutritionInformation.carbohydrates = doc.select("span[itemprop=carbohydrateContent]").text().trim();
			nutritionInformation.dietaryFiber = doc.select("span[itemprop=fiberContent]").text().trim();
			nutritionInformation.protein = doc.select("span[itemprop=proteinContent]").text().trim();
			nutritionInformation.cholestrol = doc.select("span[itemprop=cholesterolContent]").text().trim();
			nutritionInformation.sodium = doc.select("span[itemprop=sodiumContent]").text().trim();
			
			
			//create recipe
			Recipe recipe = new Recipe(title, description, pictureURL, servings, prepTime, tags, preparation, ingredients, nutritionInformation, numOfIngredients, numOfPreparationSteps);
			recipeSubscriber.onPublished(org.bson.Document.parse(gson.toJson(recipe)));
			
			} catch (Exception e) {
			System.out.println("Error for:" + recipeURL);
		}
		

	}
}

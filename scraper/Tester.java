package scraper;


public class Tester {
	public static void main(String args[]){
		
		RecipeInformationScraper scraper = new RecipeInformationScraper(new OutputToConsole());
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016835-pad-kee-mao");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016785-green-chile-breakfast-quesadillas");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1017363-date-cake-delicious");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1014119-macedonian-cheese-pie");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1013199-savory-ham-and-gruyere-bread");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/9424-eggplant-with-miso-nasu-miso");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/5686-chicken-coconut-soup");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1013598-naan-indian-flatbread");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016754-butter-chicken");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1015871-crispy-pork-cheek-belly-or-turkey-thigh-salad");
	}
}

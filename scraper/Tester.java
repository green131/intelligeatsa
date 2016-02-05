package scraper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Tester {
	public static void main(String args[]){
		
		int x = 3;
		System.out.println(x);
		try {
			BufferedReader reader = new BufferedReader(new FileReader("scraper\\urls.txt"));
			String line;
			ArrayList<String> urlList = new ArrayList<String>();
			while( (line = reader.readLine()) != null ){
				String url = "http://cooking.nytimes.com" + line;
				urlList.add(url);
			}
			
			RecipeInformationScraper scraper = new RecipeInformationScraper(new OutputToConsole());
			scraper.scrapeRecipeInformation(urlList);
			
			//print tag words map
			System.out.printf("\nscraped = %d\n",scraper.successfullyScraped);
			System.out.printf("TagWordMap:\n");
			for(String tag : scraper.tagWordMap.keySet()){
				System.out.printf("%5s : %d\n", tag, scraper.tagWordMap.get(tag));
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*RecipeInformationScraper scraper = new RecipeInformationScraper(new OutputToConsole());
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016835-pad-kee-mao");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016785-green-chile-breakfast-quesadillas");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1017363-date-cake-delicious");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1014119-macedonian-cheese-pie");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1013199-savory-ham-and-gruyere-bread");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/9424-eggplant-with-miso-nasu-miso");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/5686-chicken-coconut-soup");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1013598-naan-indian-flatbread");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1016754-butter-chicken");
		scraper.scrapeRecipeInformation("http://cooking.nytimes.com/recipes/1015871-crispy-pork-cheek-belly-or-turkey-thigh-salad");*/
	}
}

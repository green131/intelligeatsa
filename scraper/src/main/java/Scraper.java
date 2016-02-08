package main.java;

import main.java.Subscribers.MongoSubscriber;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Scraper {
	public static void main(String args[]){

		try {
			BufferedReader reader = new BufferedReader(new FileReader("scraper\\urls.txt"));
			String line;
			ArrayList<String> urlList = new ArrayList<String>();
			while( (line = reader.readLine()) != null ){
				String url = "http://cooking.nytimes.com" + line;
				urlList.add(url);
			}
      MongoSubscriber mongoSubscriber = new MongoSubscriber("localhost","27017","recipesCollection");
			RecipeInformationScraper scraper = new RecipeInformationScraper(mongoSubscriber);
			scraper.scrapeRecipeInformation(urlList);

      /*
			//print tag words map
			System.out.printf("\nscraped = %d\n",scraper.successfullyScraped);
			System.out.printf("TagWordMap:\n");
			for(String tag : scraper.tagWordMap.keySet()){
				System.out.printf("%5s : %d\n", tag, scraper.tagWordMap.get(tag));
			}
			*/
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

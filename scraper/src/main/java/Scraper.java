package main.java;

import com.mongodb.MongoCredential;
import main.java.Subscribers.ConsoleSubscriber;
import main.java.Subscribers.MongoSubscriber;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Scraper {
  public static void main(String args[]) {

    Scanner sc = new Scanner(System.in);
    System.out.println(Constants.Scraper.URLS_FILE_PATH_PROMPT);
    String urlsFilePath = sc.nextLine();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(urlsFilePath));
      String line;
      ArrayList<String> urlList = new ArrayList<String>();
      while ((line = reader.readLine()) != null) {
        String url = Constants.Scraper.NYTIMES_BASE_URL + line;
        urlList.add(url);
      }

      MongoCredential intelligeatsaDatabaseCredential = MongoCredential.createCredential(Constants.Mongo.USER,Constants.Mongo.DATABASE, Constants.Mongo.USER_PASS);
      MongoSubscriber mongoSubscriber = new MongoSubscriber(Constants.Mongo.HOST,Constants.Mongo.PORT,Constants.Mongo.DATABASE,Constants.Mongo.RECIPES_COLLECTION,intelligeatsaDatabaseCredential);
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

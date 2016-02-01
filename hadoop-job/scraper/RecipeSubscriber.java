package scraper;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by jethva on 2/1/16.
 */
public class RecipeSubscriber implements Subscriber<Document> {

    private static final MongoClient mongoClient = new MongoClient("45.55.166.1");
    private static final MongoDatabase mongoDatabase = mongoClient.getDatabase("recipes");
    private static final MongoCollection<Document> recipesCollection = mongoDatabase.getCollection("recipeCollection");

    public RecipeSubscriber(){}

    @Override
    public void onPublished(Document result) {
        recipesCollection.insertOne(result);
    }
}

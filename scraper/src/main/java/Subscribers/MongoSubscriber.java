package main.java.Subscribers;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by jethva on 2/8/16.
 */
public class MongoSubscriber implements ISubscriber {

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;
  private MongoCollection<Document> mongoCollection;
  private String host, database, collection;

  public MongoSubscriber(String host, String database, String collection) {
    this.host = host;
    this.database = database;
    this.collection = collection;
    mongoClient = new MongoClient(this.host);
    mongoDatabase = mongoClient.getDatabase(this.database);
    mongoCollection = mongoDatabase.getCollection(this.collection);
  }

  public void onPublished(String message) {
    Document document = Document.parse(message);
    mongoCollection.insertOne(document);
  }
}

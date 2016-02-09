package main.java.Subscribers;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.java.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jethva on 2/8/16.
 */
public class MongoSubscriber implements ISubscriber {

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;
  private MongoCollection<Document> mongoCollection;
  private MongoCredential databaseCredential;
  private String host, database, collection;
  private int port;

  public MongoSubscriber(String host, int port, String database, String collection, MongoCredential databaseCredential) {
    this.host = host;
    this.database = database;
    this.collection = collection;
    this.port = port;
    this.databaseCredential = databaseCredential;

    // setup authorization info for constructor of mongo client.
    List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
    credentialsList.add(this.databaseCredential);

    // create client and get collection from database
    mongoClient = new MongoClient(new ServerAddress(this.host,this.port),credentialsList);
    mongoDatabase = mongoClient.getDatabase(this.database);
    mongoCollection = mongoDatabase.getCollection(this.collection);
  }

  public void onPublished(String message) {
    Document document = Document.parse(message);
    mongoCollection.insertOne(document);
  }
}

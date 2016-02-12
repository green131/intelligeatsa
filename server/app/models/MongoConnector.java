package models;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoConnector {

  private ServerAddress mongoServerAddress;
  private MongoCredential mongoCredential;
  private MongoClient mongoClient;

  public MongoConnector() {
    if (Constants.Mongo.AUTH_ENABLED) {
      mongoCredential = MongoCredential.createCredential(
          Constants.Mongo.USER, Constants.Mongo.DATABASE, Constants.Mongo.USER_PASS
      );
    }

    mongoServerAddress = new ServerAddress(
        Constants.Mongo.HOST, Constants.Mongo.PORT
      );

    this.mongoClient = new MongoClient(
        mongoServerAddress, Arrays.asList(mongoCredential)
      );
  }

  public MongoDatabase getDatabaseByName(String databaseName) {
    return mongoClient.getDatabase(databaseName);
  }

  public MongoCollection<Document> getCollectionByName(String databaseName, String collectionName) {
    MongoDatabase db = this.getDatabaseByName(databaseName);
    return db.getCollection(collectionName);
  }

  public MongoCollection<Document> getCollectionByName(String collectionName) {
    return getCollectionByName(Constants.Mongo.DATABASE, collectionName);
  }

  public void saveDocument(String databaseName, String collectionName, Document document) {
    MongoCollection<Document> mongoCollection = getCollectionByName(databaseName, collectionName);
    mongoCollection.insertOne(document);
  }

  public void saveDocument(String collectionName, Document document) {
    saveDocument(Constants.Mongo.DATABASE, collectionName, document);
  }

  public void saveDocuments(String databaseName, String collectionName, List<Document> documents) {
    MongoCollection<Document> mongoCollection = getCollectionByName(databaseName, collectionName);
    mongoCollection.insertMany(documents);
  }

  public void saveDocuments(String collectionName, List<Document> documents) {
    MongoCollection<Document> mongoCollection = getCollectionByName(collectionName);
    mongoCollection.insertMany(documents);
  }

}

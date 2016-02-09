package controllers;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.MongoCredential;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.ServerAddress;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoConnector {

  private MongoClient mongoClient;
  private MongoCredential mongoCredential;

  public MongoConnector() {
    ClusterSettings clusterSettings = ClusterSettings.builder()
        .hosts(Arrays.asList(new ServerAddress(Constants.Mongo.HOST, Constants.Mongo.PORT)))
        .description(Constants.Mongo.DESCRIPTION)
        .build();
    MongoClientSettings settings = MongoClientSettings.builder()
        .clusterSettings(clusterSettings)
        .build();
    mongoClient = MongoClients.create(settings);
    if (Constants.Mongo.AUTH_ENABLED) {
      mongoCredential = MongoCredential.createCredential(
          Constants.Mongo.USER, Constants.Mongo.DATABASE, Constants.Mongo.USER_PASS
      );
    }
  }

  public MongoDatabase getTableByName(String tableName) {
    return mongoClient.getDatabase(tableName);
  }

  public MongoCollection<Document> getCollectionByName(String tableName, String collectionName) {
    MongoDatabase db = mongoClient.getDatabase(tableName);
    return db.getCollection(collectionName);
  }

  public MongoCollection<Document> getCollectionByName(String collectionName) {
    return getCollectionByName(Constants.Mongo.DATABASE, collectionName);
  }

  public void saveDocument(String tableName, String collectionName, Document document) {
    MongoCollection<Document> mongoCollection = getCollectionByName(collectionName);
    mongoCollection.insertOne(document, new SingleResultCallback<Void>() {
      @Override
      public void onResult(final Void result, final Throwable t) {
        System.out.println("Document inserted!");
      }
    });
  }

  public void saveDocument(String collectionName, Document document) {
    saveDocument(Constants.Mongo.DATABASE, collectionName, document);
  }

  public void saveDocuments(String collectionName, List<Document> documents) {
    MongoCollection<Document> mongoCollection = getCollectionByName(collectionName);
    mongoCollection.insertMany(documents, new SingleResultCallback<Void>() {
      @Override
      public void onResult(final Void result, final Throwable t) {
        System.out.println("Documents inserted!");
      }
    });
  }
}

package models;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.connection.ClusterSettings;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoConnector {

  private MongoClient mongoClient;

  public MongoConnector() {
    if (Constants.Mongo.AUTH_ENABLED) {
      MongoCredential mongoCredential = MongoCredential.createCredential(
          Constants.Mongo.USER, Constants.Mongo.USERS_COLLECTION, Constants.Mongo.USER_PASS
      );
    }

    ClusterSettings clusterSettings = ClusterSettings.builder()
        .hosts(Arrays.asList(new ServerAddress(Constants.Mongo.HOST, Constants.Mongo.PORT)))
        .description(Constants.Mongo.DESCRIPTION)
        .build();

    MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();
    mongoClient = MongoClients.create(settings);
  }

  public MongoDatabase getDatabaseByName(String tableName) {
    return mongoClient.getDatabase(tableName);
  }

  public MongoCollection<Document> getCollectionByName(String tableName, String collectionName) {
    MongoDatabase db = this.getDatabaseByName(tableName);
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

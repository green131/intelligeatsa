package controllers;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

import controllers.Constants.*;

public class MongoConnector {

  public boolean auth = false;
  private MongoClient mongo;
  private MongoDatabase db;

  public MongoConnector() {
    mongo = MongoClients.create(new ConnectionString(DB_ADDRESS, DB_PORT.toString()));
    db = mongoClient.getDatabase(DB_NAME);
    if (AUTH_ENABLED) {
      auth = db.authenticate(DB_USERNAME, DB_PASSWORD.toCharArray());
      if (!auth) {
        throw new SecurityException();
      }
    }
    return auth;
  }

  public DB getTableByName(String tableName) {
    return mongo.getDatabase(tableName);
  }

  public DBCollection getCollectionByName(String tableName, String collectionName) {
    DB db = mongo.getDatabase(tableName);
    return db.getCollection(collectionName);
  }

  public DBCollection getCollectionByName(String collectionName) {
    DB db = mongo.getDatabase(DB_NAME);
    return db.getCollection(collectionName);
  }

  public boolean saveDocument(String tableName, String collectionName, BasicDBObject document) {
    DB db = mongo.getDatabase(tableName);
    DBCollection table = db.getCollection(collectionName);
    return table.insert(document);
  }

  public boolean saveDocument(String collectionName, BasicDBObject document) {
    DB db = mongo.getDatabase(DB_NAME);
    DBCollection table = db.getCollection(collectionName);
    return table.insert(document);
  }
}

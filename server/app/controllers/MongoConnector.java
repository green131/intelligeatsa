package controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import controllers.Constants.*;

public class MongoConnector {

  public boolean auth = false;
  private MongoClient mongo;
  private DB db;

  public MongoConnector() {
    mongo = new MongoClient(DB_ADDRESS, DB_PORT.toString());
    db = mongoClient.getDB(DB_NAME);
    auth = db.authenticate(DB_USERNAME, DB_PASSWORD.toCharArray());
    if (!auth && AUTH_ENABLED) {
      throw new SecurityException();
    }
    return auth;
  }

  public DB getTableByName(String tableName) {
    return mongo.getDB(tableName);
  }

  public DBCollection getCollectionByName(String tableName, String collectionName) {
    DB db = mongo.getDB(tableName);
    return db.getCollection(collectionName);
  }

  public DBCollection getCollectionByName(String collectionName) {
    DB db = mongo.getDB(DB_NAME);
    return db.getCollection(collectionName);
  }

  public boolean saveDocument(String tableName, String collectionName, BasicDBObject document) {
    DB db = mongo.getDB(tableName);
    DBCollection table = db.getCollection(collectionName);
    return table.insert(document);
  }

  public boolean saveDocument(String collectionName, BasicDBObject document) {
    DB db = mongo.getDB(DB_NAME);
    DBCollection table = db.getCollection(collectionName);
    return table.insert(document);
  }
}

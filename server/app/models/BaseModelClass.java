package models;

import org.bson.Document;

public abstract class BaseModelClass {

  MongoConnector mongoConnector;
  String collection;
  Document doc;

  public BaseModelClass(MongoConnector mongoConnector, String collection) {
    this.mongoConnector = mongoConnector;
    this.collection = collection;
  }

  public BaseModelClass(MongoConnector mongoConnector, String collection, String key, String val) {
    this.mongoConnector = mongoConnector;
    this.collection = collection;
    this.doc = new Document(key, val);
  }

  public BaseModelClass(MongoConnector mongoConnector, String collection, Document doc) {
    this.mongoConnector = mongoConnector;
    this.collection = collection;
    this.doc = doc;
  }

  public BaseModelClass(MongoConnector mongoConnector, String collection, String json) {
    this.mongoConnector = mongoConnector;
    this.collection = collection;
    this.doc = importToDocument(json);
  }

  public void save() {
    mongoConnector.saveDocument(this.collection, this.doc);
  }

  public String exportToJson(Document doc) {
    return doc.toJson();
  }

  public Document importToDocument(String json) {
    return Document.parse(json);
  }

  public void addAttribute(String key, String val) {
    this.doc.append(key, val);
  }

}
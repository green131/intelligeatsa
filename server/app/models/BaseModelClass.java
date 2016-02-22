package server.app.models;

import org.bson.Document;

public abstract class BaseModelClass {

  public String collection;
  public Document doc;

  public BaseModelClass(String collection) {
    this.collection = collection;
  }

  public BaseModelClass(String collection, String key, String val) {
    this.collection = collection;
    this.doc = new Document(key, val);
  }

  public BaseModelClass(String collection, Document doc) {
    this.collection = collection;
    this.doc = doc;
  }

  public BaseModelClass(String collection, String json) {
    this.collection = collection;
    this.doc = importToDocument(json);
  }

  public String exportToString() {
    return doc.toJson();
  }

  public Document importToDocument(String json) {
    return Document.parse(json);
  }

  public void addAttribute(String key, String val) {
    this.doc.append(key, val);
  }

  public void removeAttribute(String key) {
    this.doc.remove(key);
  }

  public void changeAttribute(String key, String val) {
    if (this.doc.containsKey(key)) {
      this.removeAttribute(key);
    }
    this.addAttribute(key, val);
  }

}
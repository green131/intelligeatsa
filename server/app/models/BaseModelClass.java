package server.app.models;

import org.bson.Document;
import server.app.Global;

public abstract class BaseModelClass {

  public String collection;
  public Document doc;

  public BaseModelClass(String collection) {
    this.collection = collection;
    this.doc = null;
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
    return this.doc.toJson();
  }

  public Document importToDocument(String json) {
    return Document.parse(json);
  }

  // Always use updateAttribute to prevent conflicts!
  private void addAttribute(String key, String val) {
    this.doc.put(key, val);
  }

  public void removeAttribute(String key) {
    this.doc.remove(key);
  }

  public void updateAttribute(String key, String val) {
    if (this.doc.containsKey(key)) {
      this.removeAttribute(key);
    }
    this.addAttribute(key, val);
  }

  public Object getAttribute(Object key) { return this.doc.get(key); }

  public void saveDoc() {
    Global.mongoConnector.saveDocument(this.collection, this.doc);
  }

}
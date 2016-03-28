package server.app.models;

import server.app.Constants;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

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

  public void addAttribute(String key, String val) {
    this.doc.put(key, val);
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

  public Object getAttribute(Object key) { return this.doc.get(key); }

  public boolean hasAttribute(String key) {
    return doc.containsKey(key);
  }

  public ObjectId getId() {
    return doc.getObjectId(Constants.Mongo.ID);
  }

  /**
   * Save a model back to MongoDB
   * If this model doesn't already have an id, one is added
   * @param conn database connection
   * @return true if this model didn't exist in the database before, false if it did
   */
  public boolean persist(MongoConnector conn) {
    MongoCollection<Document> coll = conn.getCollectionByName(collection);
    if (!hasAttribute(Constants.Mongo.ID)) {
      doc.put(Constants.Mongo.ID, ObjectId.get());
    }
    return coll.findOneAndReplace(eq(Constants.Mongo.ID, getId()), doc,
        new FindOneAndReplaceOptions().upsert(true)) != null;
  }

}

package server.app.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import server.app.Constants;
import server.app.Global;

import static com.mongodb.client.model.Filters.eq;

public class User extends BaseModelClass {

  public User(Document doc) {
    super(Constants.Mongo.USERS_COLLECTION, doc);
  }

  public User(String username) {
    super(Constants.Mongo.USERS_COLLECTION);
    MongoCollection<Document> mongoCollection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);

    //create query
    Bson query = eq("username", username);

    //get user
    FindIterable<Document> iter = mongoCollection.find(query);
    doc = iter.first();
  }

  public boolean isAuthValid(String pass) {
    // TODO decrypt pass using private key
    // return whether password is valid
    if (this.doc == null || pass == null) return false;
    return this.getAttribute("password").equals(pass);
  }

  public String generateUserToken() {
    return this.getAttribute("_id").toString();
  }

}

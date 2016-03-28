package server.app.models;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;
import server.app.Constants;
import server.app.Global;
import server.app.exceptions.ServerResultException;
import static com.mongodb.client.model.Filters.eq;

public class User extends BaseModelClass {

  public User(Document doc) {
    super(Constants.Mongo.USERS_COLLECTION, doc);
  }

  public User(String username) {
    super(Constants.Mongo.USERS_COLLECTION);
    MongoCollection<Document> mongoCollection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);

    //create query
    Bson query = eq(Constants.User.ID_USER, username);

    //get user
    FindIterable<Document> iter = mongoCollection.find(query);
    doc = iter.first();
  }

  public boolean isAuthValid(String pass) {
    // TODO decrypt pass using private key
    // return whether password is valid
    if (this.doc == null || pass == null) return false;
    return this.getAttribute(Constants.User.ID_PASS).equals(pass);
  }

  public ObjectId generateUserToken() {
    return getId();
  }

  public static boolean usernameExists(String username) {
    MongoCollection<Document> mongoCollection = Global.mongoConnector.getCollectionByName(Constants.Mongo.USERS_COLLECTION);

    //create query
    Bson query = eq(Constants.User.ID_USER, username);

    //get user
    FindIterable<Document> iter = mongoCollection.find(query);
    return iter.first() != null;
  }

  public static User getUserFromToken(MongoConnector conn, ObjectId userToken) {
    Document userDoc = conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)
      .find(eq(Constants.Mongo.ID, userToken))
      .first();
    return userDoc != null ? new User(userDoc) : null;
  }

  public static User getUserFromSocialId(MongoConnector conn, String idType, String id) {
    Document userDoc = conn.getCollectionByName(Constants.Mongo.USERS_COLLECTION)
      .find(eq(idType, id))
      .first();
    return userDoc != null ? new User(userDoc) : null;
  }

  public static User getUserFromRequest(MongoConnector conn, JsonNode json) {
    if (!json.has(Constants.User.ID_TOKEN)) {
      return null;
    }
    ObjectId id;
    try {
      id = new ObjectId(json.get(Constants.User.ID_TOKEN).textValue());
    } catch (IllegalArgumentException e) {
      return null;
    }
    return getUserFromToken(conn, id);
  }

  
  public static User getUserFromJsonRequest(Request request) throws ServerResultException{
    
    //check if request is json
    JsonNode requestJson = request.body().asJson();
    if(requestJson == null) {
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Expecting Json data!")));
    }

    //check if json request contains required information
    JsonNode userIDNode = requestJson.findPath(Constants.Routes.ID_USER);
    if(!userIDNode.isTextual()){
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed request: expecting text information!")));
    }

    //parse userID
    String userID = userIDNode.textValue();
    ObjectId rID = null, uID = null;
    try {
      uID = new ObjectId(userID);
    } catch (IllegalArgumentException e) {
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Malformed user id, not hexadecimal")));
    }

    //check if the given user exists
    User user = User.getUserFromToken(Global.mongoConnector, uID);
    if(user == null){
      throw new ServerResultException(Results.badRequest(new ObjectMapper().createObjectNode().put(Constants.Generic.ERROR, "Could not find user matching id")));
    }

    return user;
    
  }
  
  
  @Override
  public boolean equals(Object other) {
    if (other instanceof User) {
      User otherUser = (User) other;
      return doc.equals(otherUser.doc);
    }
    return false;
  }

}

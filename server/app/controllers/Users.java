package server.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.models.User;


//API for user information
public class Users extends Controller {
  public static Result loginUser() {
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    // check for social login
    String socialIdName = null;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Can only specify 1 social id!"));
    } else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdName = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdName = Constants.User.ID_GOOGLE;
    }
    if (socialIdName != null) {
      User user = User.getUserFromSocialId(Global.mongoConnector, socialIdName, requestJson.get(socialIdName).asText());
      if (user != null) {
        return ok(new ObjectMapper().createObjectNode()
            .put(Constants.User.ID_TOKEN, user.generateUserToken().toHexString())
            .put(Constants.User.ID_USER, user.doc.getString(Constants.User.ID_USER)));
      } else {
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "bad social id"));
      }
    }

    //check if request is json array
    JsonNode userNode = requestJson.findPath(Constants.User.ID_USER);
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);
    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    String username = userNode.textValue();
    String password = passNode.textValue();

    User u = new User(username);
    if (u.isAuthValid(password)) {
      return ok(new ObjectMapper().createObjectNode()
          .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString())
          .put(Constants.User.ID_USER, u.doc.getString(Constants.User.ID_USER)));
    }
    return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "invalid credentials"));
  }

  public static Result registerUser() {
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    //check if request is json array
    JsonNode userNode = requestJson.findPath(Constants.User.ID_USER);
    if (!userNode.isTextual()) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting text user name"));
    }
    if (User.usernameExists(userNode.asText())) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Username already taken"));
    }

    String socialIdName = null;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Can only specify 1 social id!"));
    } else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdName = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdName = Constants.User.ID_GOOGLE;
    }
    if (socialIdName != null) {
      String socialId = requestJson.get(socialIdName).asText();
      if (User.getUserFromSocialId(Global.mongoConnector, socialIdName, socialId) != null) {
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "social id already taken"));
      }
      User u = new User(userNode.asText());
      u.doc = new Document();
      u.addAttribute(Constants.User.ID_USER, userNode.asText());
      u.addAttribute(socialIdName, socialId);
      u.persist(Global.mongoConnector);
      return ok(new ObjectMapper().createObjectNode()
        .put(Constants.User.ID_USER, userNode.asText())
        .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString()));
    }

    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);
    if(!passNode.isTextual()) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    String username = userNode.textValue();
    String password = passNode.textValue();
    if (password.length() < 5) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Password needs to be 5 characters or more!"));
    }

    // create new user doc and store in db
    User u = new User(username);
    u.doc = new Document();
    u.addAttribute(Constants.User.ID_USER, username);
    u.addAttribute(Constants.User.ID_PASS, password);
    Global.mongoConnector.saveDocument(u.collection, u.doc);

    // check doc was saved correctly and return token
    if (u.isAuthValid(password)) {
      return ok(new ObjectMapper().createObjectNode()
          .put(Constants.User.ID_TOKEN, u.generateUserToken().toHexString())
          .put(Constants.User.ID_USER, u.doc.getString(Constants.User.ID_USER)));
    }
    return badRequest(new ObjectMapper().createObjectNode()
        .put(Constants.Generic.ERROR, "could not save new user account"));
  }

  public static Result linkUserAccount() {
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting Json data!"));
    }

    User user = User.getUserFromRequest(Global.mongoConnector, requestJson);
    if (user == null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting user info!"));
    }

    String socialIdName;
    if (requestJson.has(Constants.User.ID_FB) && requestJson.has(Constants.User.ID_GOOGLE)) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting either fb user id or google user id"));
    }
    else if (requestJson.has(Constants.User.ID_FB)) {
      socialIdName = Constants.User.ID_FB;
    } else if (requestJson.has(Constants.User.ID_GOOGLE)) {
      socialIdName = Constants.User.ID_GOOGLE;
    } else {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Expecting either fb user id or google user id"));
    }
    String socialId = requestJson.get(socialIdName).asText();
    User existingUser = User.getUserFromSocialId(Global.mongoConnector, socialIdName, socialId);
    if (existingUser != null) {
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Social id already taken"));
    }
    user.changeAttribute(socialIdName, socialId);
    user.persist(Global.mongoConnector);
    return ok();
  }
}

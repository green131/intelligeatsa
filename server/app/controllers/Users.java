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
    JsonNode passNode = requestJson.findPath(Constants.User.ID_PASS);

    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest(new ObjectMapper().createObjectNode()
          .put(Constants.Generic.ERROR, "Malformed request: expecting text information!"));
    }

    String username = userNode.textValue();
    String password = passNode.textValue();

    if (User.usernameExists(username)) {
      return badRequest("Username already exists!");
    } else if (password.length() < 5) {
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

}

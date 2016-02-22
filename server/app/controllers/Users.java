package server.app.controllers;

import java.util.ArrayList;
import server.app.Global;
import org.bson.types.ObjectId;
import server.app.models.User;
import server.app.Constants;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.data.DynamicForm;
import play.data.Form;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;
import play.libs.Json.*;
import java.util.Arrays;


//API for user information
public class Users extends Controller {

  public static Result loginUser() {
    //check if request is json
    JsonNode requestJson = request().body().asJson();
    if(requestJson == null) {
      return badRequest("Expecting Json data!");
    }

    //check if request is json array
    JsonNode userNode = requestJson.findPath("user");
    JsonNode passNode = requestJson.findPath("pass");
    if(!userNode.isTextual() || !passNode.isTextual()){
      return badRequest("Expecting Json array!");
    }

    String username = userNode.textValue();
    String password = passNode.textValue();

    User u = new User(username);
    if (u.isAuthValid(password)) {
      return ok(u.generateUserToken());
    }
    return badRequest(new ObjectMapper().createObjectNode()
        .put("error", "invalid credentials"));
  }

}

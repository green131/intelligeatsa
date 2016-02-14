package server.app.controllers;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.model.FindOptions;

import static com.mongodb.client.model.Filters.*;

import com.sun.org.apache.xpath.internal.operations.And;

import server.app.models.Constants;
import server.app.models.MongoConnector;
import server.app.models.Recipe;
import play.mvc.Controller;
import play.mvc.Result;

public class HelloWorld extends Controller {

  public static Result show() {
    String ret = "Hello World!";
    return ok(ret);
  }
}

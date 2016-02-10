package controllers;

import models.MongoConnector;

import models.Recipe;
import play.mvc.Controller;
import play.mvc.Result;

public class HelloWorld extends Controller {

  public static Result show() {
    MongoConnector mdb = new MongoConnector();
    Recipe r = new Recipe(mdb);
    String ret = "Hello World!";
    return ok(ret);
  }
}

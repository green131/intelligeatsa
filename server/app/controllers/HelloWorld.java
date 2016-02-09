package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import controllers.MongoConnector.*;

public class HelloWorld extends Controller {
  public static Result show() {
    MongoConnector mdb = new MongoConnector();
    String ret = "Hello World!";
    return ok(ret);
  }
}

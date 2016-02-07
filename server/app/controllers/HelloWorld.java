package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import models.Recipe;

public class HelloWorld extends Controller {
  public static Result show() {
    String ret;
    Recipe r = new Recipe();
    r.name = "Pasta";
    ret = r.name;
    ret += "\nHello World!";
    return ok(ret);
  }
}

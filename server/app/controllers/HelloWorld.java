package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class HelloWorld extends Controller {
    public static Result show() {
        return ok("Hello, World!");
    }
}

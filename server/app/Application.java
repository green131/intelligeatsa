package server.app;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

  public static Result preflight(String path) {
    response().setHeader("Access-Control-Allow-Origin", "*");
    response().setHeader("Allow", "*");
    response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
    response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
    return ok();
  }
}

package server.app;

import play.Application;
import play.GlobalSettings;
import server.app.models.MongoConnector;
import server.app.models.Recipe;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import org.bson.types.ObjectId;

public class Global extends GlobalSettings {

  public static MongoConnector mongoConnector;

  @Override
  public void onStart(Application app) {
    mongoConnector = new MongoConnector();
    // create full text search index on recipes
    Recipe.setupSearchIndex(mongoConnector);
  }

  // add cors header to each response
  @Override
  public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
    return new ActionWrapper(super.onRequest(request, actionMethod));
  }

  /* cors action*/
  private class ActionWrapper extends Action.Simple {
    public ActionWrapper(Action<?> action) {
      this.delegate = action;
    }

    @Override
    public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
      Promise<Result> result = this.delegate.call(ctx);
      Http.Response response = ctx.response();
      response.setHeader("Access-Control-Allow-Origin", "*");
      return result;
    }
  }
}

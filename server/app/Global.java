package server.app;

import play.Application;
import play.GlobalSettings;
import server.app.models.MongoConnector;
import server.app.models.Recipe;

public class Global extends GlobalSettings {

  public static MongoConnector mongoConnector;

  @Override
  public void onStart(Application app) {
    mongoConnector = new MongoConnector();
    // create full text search index on recipes
    Recipe.setupSearchIndex(mongoConnector);
  }

}

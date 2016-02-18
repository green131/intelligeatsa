package server.app;

import server.app.models.MongoConnector;
import server.app.models.Recipe;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
  @Override
  public void onStart(Application app) {
    MongoConnector conn = new MongoConnector();
    // create full text search index on recipes
    Recipe.setupSearchIndex(conn);
  }
}


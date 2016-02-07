package models;

import models.Constants.*;

public class BaseModelClass {

  public BaseModelClass() {

    if (this.jongo == false) {
      this.db = new MongoClient().getDB(DB_NAME);
      this.jongo = new Jongo(db);
    }

  }

  public int Save() {
  }

}
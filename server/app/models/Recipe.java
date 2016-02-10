package models;

public class Recipe extends BaseModelClass {

  public Recipe(MongoConnector mongoConnector) {
    super(mongoConnector, Constants.Mongo.RECIPES_COLLECTION);
  }

}

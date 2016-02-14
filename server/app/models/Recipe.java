package models;

public class Recipe extends BaseModelClass {

  public Recipe(MongoConnector mongoConnector) {
    super(Constants.Mongo.RECIPES_COLLECTION);
  }

}

package server.app.models;

public class Ingredient {
  public String quantity;
  public String item;
  
  public Ingredient(String quantity, String item){
    this.quantity = quantity;
    this.item = item;
  }
  
  @Override
  public String toString(){
    return "quantity: " + quantity + ", item: " + item;
  }
}

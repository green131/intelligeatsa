package main.java;

public class Ingredient {
  String quantity, item, optionalInformation;

  public Ingredient(String quantity, String item, String optionalInformation) {
    this.quantity = quantity;
    this.item = item;
    this.optionalInformation = optionalInformation;
  }

  @Override
  public String toString() {
    return "quantity: " + quantity + ", item: " + item + ", optionalInformation: " + optionalInformation;
  }
}

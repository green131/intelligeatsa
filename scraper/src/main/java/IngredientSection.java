package main.java;

import java.util.ArrayList;

//i.e: Making the sauce, or making the bread
public class IngredientSection {
	String sectionName;
	ArrayList<Ingredient> sectionIngredients;
	
	public IngredientSection(String sectionName, ArrayList<Ingredient> sectionIngredients){
		this.sectionName = sectionName;
		this.sectionIngredients = sectionIngredients;
	}
	
	@Override
	public String toString(){
		return "(sectionName = " + sectionName + ", sectionIngredients: " + sectionIngredients + ")";
	}
	
}

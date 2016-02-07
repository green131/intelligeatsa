package controllers;

import java.util.ArrayList;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;
import play.data.DynamicForm;
import play.data.Form;

public class HomePage extends Controller {
	
	public final static String defaultCuisineCarousels[] = {"Indian", "Mexican", "American", "Vegetarian"};

	
    public static Result getHomepage() {
    	//get user information
        DynamicForm dynamicForm = play.data.Form.form().bindFromRequest();
        String user = dynamicForm.get("user");
        
        //get cuisine carousels
    	ArrayList<String> listOfCuisineCarousels = new ArrayList<String>();
        if(!isNullOrBlank(user)){
        	addRecommendationCarousel(user, listOfCuisineCarousels);
        } 
        addDefaultCuisineCarousels(listOfCuisineCarousels);
        
        String ret = "[";
        for(int i=0; i<listOfCuisineCarousels.size(); i++){
        	String s = listOfCuisineCarousels.get(i);
        	ret += s;
        	if(i != listOfCuisineCarousels.size()-1){
        		ret += ",";
        	}
        }
        ret += "]";
        
        
        System.out.println(ret);
        return ok(ret);
    }
    
    
    
    private static void addDefaultCuisineCarousels(ArrayList<String> listOfCuisineCarousels){
    	for(String tag : defaultCuisineCarousels){
    		//TODO: get cuisine carouseld from database
    		String temp = "{cool="+tag+"}";
    		listOfCuisineCarousels.add(temp);
    	}
    }
    
    
    
    private static void addRecommendationCarousel(String user, ArrayList<String> listOfCuisineCarousels){
    	if(isNullOrBlank(user)){
    		return;
    	}
		String temp = "{recommendations, wow!}";
		listOfCuisineCarousels.add(temp);
    	//TODO: add recommendation carousel
    }
    

    
    private static boolean isNullOrBlank(String s){
    	return s==null || s.trim().length()==0;
    }

}











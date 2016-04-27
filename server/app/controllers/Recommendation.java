package server.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import server.app.Constants;
import server.app.Global;
import server.app.exceptions.ServerResultException;
import server.app.models.Ingredient;
import server.app.models.Recipe;
import server.app.models.User;
import java.util.ArrayList;
import java.util.List;
import java.security.cert.Certificate;
import java.net.URL;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.MalformedURLException;
import com.fasterxml.jackson.core.*;

import static com.mongodb.client.model.Filters.eq;

//API for user information
public class Recommendation extends Controller {

  static {
      //for localhost testing only
      javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new javax.net.ssl.HostnameVerifier(){

          public boolean verify(String hostname,
                  javax.net.ssl.SSLSession sslSession) {
              if (hostname.equals("localhost")) {
                  return true;
              }
              return false;
          }
      });
  }

  public static Result getRecommendations(){
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode requestJson = request().body().asJson();
    if(!requestJson.has(Constants.Recommendation.RECIPES_FIELD_NAME)){
      return badRequest(objectMapper.createObjectNode()
        .put(Constants.Generic.ERROR, "bad json schema, no recipes"));
    }
    // debug
    JsonNode recipeIdArray = requestJson.findPath(Constants.Recommendation.RECIPES_FIELD_NAME);
    ArrayList<String> recipeIds = new ArrayList<String>();
    for(JsonNode recipeIdNode : recipeIdArray){
      recipeIds.add(recipeIdNode.asText());
      System.out.println(recipeIdNode.asText());
    }
    // end debug
    JsonNode engineResponse = forwardRequest(objectMapper, requestJson);
    JsonNode recipeScoresArray = engineResponse.findPath("recipeScores");
    ArrayList<Document> recommendedRecipesList = new ArrayList<Document>();
    for(JsonNode currentRecipe : recipeScoresArray){
      String currentRecipeId = currentRecipe.findPath("recipe").asText();
      ObjectId oid;
      try {
        oid = new ObjectId(currentRecipeId);
      } catch (IllegalArgumentException e) {
        return badRequest(new ObjectMapper().createObjectNode()
            .put(Constants.Generic.ERROR, "error with getting recommended recipes info"));
      }
      Recipe currentRecipeInfo = Recipe.getRecipeById(Global.mongoConnector,oid);
      recommendedRecipesList.add(new Document()
                  .append(Constants.Recipe.KEY_ID,currentRecipeId)
                  .append(Constants.Recipe.KEY_TITLE, currentRecipeInfo.getAttribute(Constants.Recipe.KEY_TITLE))
                  .append(Constants.Recipe.KEY_DESCRIPTION, currentRecipeInfo.getAttribute(Constants.Recipe.KEY_DESCRIPTION))
                  .append(Constants.Recipe.KEY_PICTUREURL, currentRecipeInfo.getAttribute(Constants.Recipe.KEY_PICTUREURL))
              );
    }
    try{
    ObjectMapper mapper = new ObjectMapper();
    String recommendedRecipesJson = mapper.writeValueAsString(recommendedRecipesList);

    JsonNode recommendedRecipesNode = mapper.readTree(recommendedRecipesJson);

    ObjectNode retNode = mapper.createObjectNode();
    retNode.put("recommendations", recommendedRecipesNode);
    return ok(retNode);
    }
    catch(Exception e){
      e.printStackTrace();
      return internalServerError();
    }
  }

  private static JsonNode forwardRequest(ObjectMapper objectMapper, JsonNode requestJson){
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[]{
      new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return null;
          }
          public void checkClientTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }
          public void checkServerTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }
      }
    };

    // Install the all-trusting trust manager
    try {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    String engineUrl = "https://localhost:8000/queries.json";
    try{
      URL url = new URL(engineUrl);
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
      objectMapper.writeValue(wr,requestJson);
      wr.flush();
      wr.close();
      int responseCode = conn.getResponseCode();
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
      }
      in.close();
      System.out.println(response);
      JsonNode engineResponse = objectMapper.readTree(response.toString());
      return engineResponse;
    }catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
  }
}
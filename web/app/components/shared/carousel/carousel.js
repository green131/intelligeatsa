'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('carousel',{
  templateUrl:'components/shared/carousel/carouselTemplate.html',
  controller:CarouselController,
  bindings:{
    cuisineType: '@',
    title: '@'
  }
});

  /**
  * Carousel Controller. 
  * @constructor
  * @param {$http} $http - angular http service
  * @param {mongUtils} mongoUtils - mongoUtils service
  */

function CarouselController($http,mongoUtils, userSession, groceryList, saveList, searchLog){
  var ctrl = this;
  ctrl.recommendationMode = false;
  if(ctrl.cuisineType != 'RECOMMENDATION_MODE'){
    var recipesByTagUrl = "http://localhost:8080/recipe/tags/"+ ctrl.cuisineType + "/0/25";
    $http({
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    url: recipesByTagUrl,
  }).then(function successCallback(response) {
      ctrl.recipes = response.data;
    }, function errorCallback(response) {
      console.log(response);
    });
  }else{
    ctrl.recommendationMode = true;
    ctrl.recommenderJobData = [];

    // call the recommendation api after aggregating data
    var searchRecipeIds = searchLog.getLog();
    for(var i=0;i<searchRecipeIds.length;i++){
      ctrl.recommenderJobData.push(searchRecipeIds[i]);
    }

    var user = userSession.getUser();
    var userRatings = user.ratingList;
    for(var i=0;i<userRatings.length;i++){
      if(userRatings[i].myRating >= 3){
        ctrl.recommenderJobData.push(userRatings[i].recipeID);
      }
    }
    console.log(userRatings);
    var recommenderUrl = "http://localhost:8080/recommendations/";
    groceryList.get(function success(response){
      var gListRecipes = response.recipeIDList;
      for(var i=0;i<gListRecipes.length;i++){
        ctrl.recommenderJobData.push(gListRecipes[i].id);
      }
      saveList.get(function(response){
         var saveListRecipes = response.recipeIDList;
         for(var i=0;i<saveListRecipes.length;i++){
            ctrl.recommenderJobData.push(saveListRecipes[i].id);
         }
         ctrl.getRecommendations();
      });
    });
  }

  ctrl.getRecommendations = function(){
    if(ctrl.recommenderJobData.length == 0){
      return;
    }
    $http({
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    url: recommenderUrl,
    data: {
      'recipes': ctrl.recommenderJobData,
      'num':20
    },
    }).then(function successCallback(response) {
      ctrl.recipes = response.data.recommendations;
      console.log(response.data);
    }, function errorCallback(response) {
      console.log(response);
    });
  }

  ctrl.generateRecipePageUrl = function(mongoIdObj){
    return '#/recipe/' + mongoUtils.mongoIdObjToString(mongoIdObj);
  };

}

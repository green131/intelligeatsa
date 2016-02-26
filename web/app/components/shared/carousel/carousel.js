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


function CarouselController($http,mongoUtils){
  var ctrl = this;
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

  ctrl.generateRecipePageUrl = function(mongoIdObj){
    return '#/recipe/' + mongoUtils.mongoIdObjToString(mongoIdObj);
  };

}

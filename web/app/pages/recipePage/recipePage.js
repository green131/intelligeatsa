'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/recipe/:id',{
    templateUrl: 'pages/recipePage/recipePage.html',
    controller: RecipePageController,
    controllerAs: '$ctrl'
  });
}]);

function RecipePageController($http, $routeParams){
  var ctrl = this;
  var recipeUrl = "http://localhost:8080/recipe/id/" + $routeParams.id;
  ctrl.recipe = '';
  ctrl.instructionList=[];
  $http({
  method: 'GET',
  url: recipeUrl,
  }).then(function successCallback(response) {
      ctrl.recipe = response.data;
      ctrl.instructionList = ctrl.recipe.preparation[0];
      console.log(response);
  }, function errorCallback(response){
    console.log(response);
  });
}

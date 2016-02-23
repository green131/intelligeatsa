'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/search/:query',{
    templateUrl: 'pages/searchResultsPage/searchResultsPage.html',
     controller: new SearchResultsPageController(),
     controllerAs: '$ctrl'
  });
}]);

function SearchResultsPageController($http,$routeParams){
  var ctrl = this;
  ctrl.page = 0;
  ctrl.showNextButton = false;
  ctrl.query = $routeParams.query;
  ctrl.rangeStart = 0;
  ctrl.rangeEnd = 20;
  ctrl.getRecipes();
}

SearchResultsPageController.prototype.next = function(){
  if(ctrl.recipes.length == 20){
  ctrl.rangeStart+=20;
  ctrl.rangeEnd+=20;
  ctrl.getRecipes();
  }
};
SearchResultsPageController.prototype.previous = function(){
  if(ctrl.rangeStart > 0 && ctrl.rangeEnd > 20){
  ctrl.rangeStart-=20;
  ctrl.rangeEnd-=20;
  ctrl.getRecipes();
  }
};

SearchResultsPageController.prototype.getRecipes = function(){
  var recipeSearchUrl = 'http://localhost:8080/recipe/search/'+ ctrl.query + '/'+ctrl.rangeStart+'/'+ctrl.rangeEnd;
  $http({
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  url: recipeSearchUrl,
}).then(function successCallback(response) {
    ctrl.recipes = response.data;
    if(ctrl.recipes.length < 20){
      ctrl.lastPage = true;
    }
  }, function errorCallback(response) {
    console.log(response);
  });
};

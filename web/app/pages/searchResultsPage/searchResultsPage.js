'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/search/:query',{
    templateUrl: 'pages/searchResultsPage/searchResultsPage.html',
     controller: SearchResultsPageController,
     controllerAs: '$ctrl'
  });
}]);

function SearchResultsPageController($http,$routeParams){
  var ctrl = this;
  ctrl.query = $routeParams.query;
  ctrl.rangeStart = 0;
  ctrl.rangeEnd = 20;
  ctrl.next = function(){
    ctrl.rangeStart+=20;
    ctrl.rangeEnd+=20;
    ctrl.getRecipes();
  };
  ctrl.previous = function(){
    if(ctrl.rangeStart > 0 && ctrl.rangeEnd > 20){
    ctrl.rangeStart-=20;
    ctrl.rangeEnd-=20;
    ctrl.getRecipes();
    }
  };

  ctrl.getRecipes = function(){
    var recipeSearchUrl = 'http://localhost:8080/recipe/search/'+ ctrl.query + '/'+ctrl.rangeStart+'/'+ctrl.rangeEnd;
    $http({
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    url: recipeSearchUrl,
  }).then(function successCallback(response) {
      ctrl.recipes = response.data;
    }, function errorCallback(response) {
      console.log(response);
    });
  };
  
  ctrl.getRecipes();
}

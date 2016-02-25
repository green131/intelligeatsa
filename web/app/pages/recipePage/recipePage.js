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

function RecipePageController($http){
  var ctrl = this;
}

'use strict';

var containersModule = angular.module('intelligeatsa.containers');

// routes for the recipePage
containersModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/recipe/:id',{
    templateUrl: 'containers/recipePage/recipePage.html',
     controller: RecipePageController
  });
}]);

function RecipePageController($http){
  var ctrl = this;
}

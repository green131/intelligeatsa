'use strict';

var containersModule = angular.module('intelligeatsa.containers');

// routes for the home page
containersModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/home',{
    templateUrl: 'containers/homePage/homePage.html',
     controller: HomePageController
  });
}]);

function HomePageController($http){
  var ctrl = this;
}

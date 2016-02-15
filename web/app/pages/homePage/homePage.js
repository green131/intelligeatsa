'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/home',{
    templateUrl: 'pages/homePage/homePage.html',
    controller: HomePageController,
    controllerAs: '$ctrl'
  });
}]);

function HomePageController($http){
  var ctrl = this;
}

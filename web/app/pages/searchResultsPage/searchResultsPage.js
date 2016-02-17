'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/recipe/:id',{
    templateUrl: 'pages/searchResultsPage.html',
     controller: SearchResultsPageController
  });
}]);

function SearchResultsPageController($http){
  var ctrl = this;
}

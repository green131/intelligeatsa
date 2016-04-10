'use strict';

angular.module('intelligeatsa.pages')
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/saveList',{
    templateUrl: 'pages/saveListPage/saveListPageTemplate.html',
    controller: SaveListPageController,
    controllerAs: '$ctrl'
  });
}]);

function SaveListPageController($http){
  var ctrl = this;
}

'use strict';

angular.module('intelligeatsa.pages')
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/saveList',{
    templateUrl: 'pages/saveListPage/saveListPageTemplate.html',
    controller: SaveListPageController,
    controllerAs: '$ctrl'
  });
}]);

function SaveListPageController($http, saveList){
  var ctrl = this;
  var saveListObj = null;
  saveList.get(function success(resp){
    console.log(resp);
    saveListObj = resp;
  }, function error(err){
    console.log(err);
  });


}

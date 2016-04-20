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
  ctrl.recipeIDList = null;
  ctrl.saveCount = 0;

  ctrl.fetchList = function(){
    saveList.get(function success(resp){
      ctrl.recipeIDList = resp.recipeIDList;
      ctrl.saveCount = ctrl.recipeIDList.length;
      console.log(ctrl.recipeIDList);
    }, function error(err){
      console.log(err);
    });
  };

  ctrl.removeRecipe = function(recipeId){
    saveList.removeRecipe(recipeId, function(){
      // now re-fetch
      ctrl.fetchList();
    }, function(err){
      console.log(err);
    });
  };

  // init
  ctrl.fetchList();
}

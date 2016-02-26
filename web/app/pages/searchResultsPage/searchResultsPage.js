'use strict';

angular.module('intelligeatsa.pages')
.constant('searchResultsBatchSize',20)
.config(['$routeProvider',function($routeProvider) {
  $routeProvider.when('/search/:query',{
    templateUrl: 'pages/searchResultsPage/searchResultsPage.html',
    controller: 'SearchResultsPageController',
    controllerAs: '$ctrl'
  });
}])
.controller('SearchResultsPageController',['$http','search','mongoUtils','searchResultsBatchSize','$routeParams',SearchResultsPageController]);

function SearchResultsPageController($http,search,mongoUtils,searchResultsBatchSize,$routeParams){
  var ctrl = this;
  ctrl.query = $routeParams.query;
  var batchSize = searchResultsBatchSize;
  var paginator = search.query(ctrl.query,batchSize);

  var paginateSuccess = function(data){
    ctrl.recipes = data;
    if(ctrl.recipes.length < batchSize){
      ctrl.lastPage = true;
    }else{
      ctrl.lastPage = false;
    }
  };

  var paginateError = function(response){

    console.log('no more results');
  };

  ctrl.next = function(){
    paginator.next(paginateSuccess, paginateError);
  };
  ctrl.previous = function(){
    paginator.previous(paginateSuccess,paginateError);
  };

  // first page.
  paginator.next(paginateSuccess,paginateError);

  // quick hack
  ctrl.generateRecipePageUrl = function(mongoIdObj){
    return '#/recipe/' + mongoUtils.mongoIdObjToString(mongoIdObj);
  };

}

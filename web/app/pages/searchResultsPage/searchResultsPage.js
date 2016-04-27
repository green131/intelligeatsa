'use strict';

angular.module('intelligeatsa.pages')
.constant('searchResultsBatchSize',20)
.config(['$routeProvider',function($routeProvider) {
  $routeProvider.when('/search/:query/:searchType',{
    templateUrl: 'pages/searchResultsPage/searchResultsPage.html',
    controller: 'SearchResultsPageController',
    controllerAs: '$ctrl'
  });
}])
.controller('SearchResultsPageController',['$http','search','mongoUtils','searchResultsBatchSize','searchLog','$routeParams',SearchResultsPageController]);

function SearchResultsPageController($http,search,mongoUtils,searchResultsBatchSize,searchLog,$routeParams){
  var ctrl = this;
  ctrl.searchType = $routeParams.searchType;
  ctrl.query = $routeParams.query;
  var batchSize = searchResultsBatchSize;
  var paginator = search.query({
    query:ctrl.query,
    searchType:ctrl.searchType
    }
    ,batchSize);
  var sortAlpha = false;

  // first page.
  function init(){
    paginator.next(function successInitWrapper(data){
      // push query to aggregate laster
      if(data.length > 0){
        searchLog.pushQuery(data[0]._id);
      }
      paginateSuccess(data);
    },function errorInitWrapper(response){
      paginateError(response);
    });
  }

  var paginateSuccess = function(data){
    data.forEach(function(recipe){
      if(recipe.title == null){
        recipe.title='NO TITLE';
      }
    })
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

  // quick hack
  ctrl.generateRecipePageUrl = function(mongoIdObj){
    return '#/recipe/' + mongoUtils.mongoIdObjToString(mongoIdObj);
  };

  ctrl.sortAlpha = function(){
    if(sortAlpha){
      // remove sorting alpha
      sortAlpha = false;
      paginator = search.query({
        query:ctrl.query,
        searchType:ctrl.searchType
      }
      ,batchSize);
      $('#sortAlphaButton').html('Sort Alphabetically');
    }else{
      sortAlpha = true;
      paginator = search.query({
        query:ctrl.query,
        searchType:ctrl.searchType,
        sort:'alpha'
      }
      ,batchSize);
      $('#sortAlphaButton').html('Revert');
    }
    paginator.next(paginateSuccess,paginateError);
   };

   // on page load
  init();
}

'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('searchBar',{
  templateUrl:'public/components/shared/searchBar/searchBarTemplate.html',
  controller:SearchBarController,
  bindings:{
    placeholder: '@'
  }
});

function SearchBarController($window,$http){
  var ctrl = this;
  ctrl.search = function(){
    $window.location.href= '#/search/'+ctrl.searchQuery;
  }
}

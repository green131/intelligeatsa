'use strict';

angular.module('intelligeatsa.pages')
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/groceryList',{
    templateUrl: 'pages/groceryListPage/groceryListPage.html',
    controller: GroceryListPageController,
    controllerAs: '$ctrl'
  });
}]);

function GroceryListPageController($http,groceryList){
  var ctrl = this;
  groceryList.get(function success(response){
    ctrl.groceryList = response;
    ctrl.groceryList = ctrl.groceryList.filter(function(listElement){
      return (listElement.quantity.trim().length > 0 && listElement.item.trim().length > 0);
    });
  }, function error(err){
    console.log(error);
  });
}

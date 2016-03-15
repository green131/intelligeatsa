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
  var fetch = function(){
    groceryList.get(function success(response){
      ctrl.recipesInfo = response.recipeIDList;
      ctrl.groceryList = response.ingredients.filter(function(listElement){
        return (listElement.quantity.trim().length > 0 && listElement.item.trim().length > 0);
      });
    }, function error(err){
      $('#loginModal').modal('show');
      console.log(error);
    });
  };

  ctrl.removeRecipe = function(recipeId){
    console.log('removing ' + recipeId );
    groceryList.remove(recipeId, function success(){
      fetch(); // fetch new result again.
    }, function error(){
        $('#loginModal').modal('show');
    });
    console.log('fetched again');
  };

  fetch();
}

angular.module('intelligeatsa.services')
.constant('apiGroceryListBaseUrl','http://localhost:8080/user/groceryList')
.factory('groceryList',GroceryListServiceFactory);

function GroceryListServiceFactory($http,apiGroceryListBaseUrl, userSession){
  var groceryListService = function(){
    var groceryList = null;

    this.get = function(successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiGroceryListBaseUrl),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        groceryList = response.data;
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
    };

    this.add = function(recipeId){

    };

    this.remove = function(recipeId){

    };

    this.clear = function(){
      groceryList.forEach(function(listItem){

      });
    };
  };
  return new groceryListService();
};

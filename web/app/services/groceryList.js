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
        successCallback(groceryList);
      }, function error(response){
        errorCallback(response);
      });
    };

    this.contains = function(recipeId, successCallback, errorCallback){
      this.get(function success(){
        var recipesInfo = groceryList.recipeIDList;
        for(var i=0;i<recipesInfo.length;i++){
          if(recipeId == recipesInfo[i].id){
            return successCallback(true);
          }
        }
        return successCallback(false);
      }, function error(){
        errorCallback();
      });
    };

    this.add = function(recipeId, successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiGroceryListBaseUrl + '/add/' +recipeId),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        successCallback(); // callback to know when success happened.
      }, function error(response){
        errorCallback();
      });
    };

    this.remove = function(recipeId, successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiGroceryListBaseUrl + '/remove/' +recipeId),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        successCallback();
      }, function error(response){
        errorCallback();
      });
    };

    this.clear = function(){
      groceryList.forEach(function(listItem){

      });
    };
  };
  return new groceryListService();
};

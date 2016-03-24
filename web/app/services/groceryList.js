angular.module('intelligeatsa.services')
.constant('apiGroceryListBaseUrl','http://localhost:8080/user/groceryList')
.factory('groceryList',GroceryListServiceFactory);

function GroceryListServiceFactory($http,apiGroceryListBaseUrl, userSession){

  /**
  * grocery list service, handles CRUD with user grocery list
  * @constructor
  */
  var groceryListService = function(){
    var groceryList = null;

    /**
    * gets the user grocery list
    * @function
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */
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

    /**
    * checks if a recipe is in the user's groceryList
    * @function
    * @param {string} recipeId - recipe id to check
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */
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

    /**
    * add a recipe to the grocery list
    * @function
    * @param {string} recipeId - recipe id to check
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */

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

    /**
    * remove recipe from grocery list
    * @function
    * @param {string} recipeId - recipe id to check
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */

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
  };
  return new groceryListService();
};

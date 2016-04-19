angular.module('intelligeatsa.services')
.constant('apiSaveListBaseUrl','http://localhost:8080/user/savedList')
.factory('saveList',SaveListServiceFactory);

function SaveListServiceFactory($http,apiSaveListBaseUrl, userSession){

  var saveListService = function(){

    var saveList = null;

    this.get = function(successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiSaveListBaseUrl),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        saveList = response.data;
        successCallback(saveList);
      }, function error(response){
        errorCallback(response);
      });
    };

    this.addRecipe = function(recipeId, successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiSaveListBaseUrl + '/add/' + recipeId),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        successCallback();
      }, function error(response){
        errorCallback(response);
      });
    };


    this.removeRecipe = function(recipeId, successCb, errorCb){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiSaveListBaseUrl + '/remove/' + recipeId),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        successCallback();
      }, function error(response){
        errorCallback(response);
      });
    };

    this.contains = function(recipeId, successCallback, errorCallback){
      var user = userSession.getUser();
      if(!user){
        console.log('no user');
        return;
      }
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiSaveListBaseUrl),
        data:{
          userID: user.token
        }
      }).then(function success(response){
        saveList = response.data;
        var foundFlag = false;
        for(var i=0;i<saveList.recipeIDList.length;i++){
          if(saveList.recipeIDList[i].id == recipeId){
            foundFlag = true;
            break;
          }
        }
        successCallback(found);
      }, function error(response){
        errorCallback(response);
      });
    };

  };

  return new saveListService();
}

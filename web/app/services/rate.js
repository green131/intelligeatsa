'use strict';

angular.module('intelligeatsa.services')
.constant('apiRateUrl','http://localhost:8080/recipe/')
.factory('rate',['$http', 'apiRateUrl', RateServiceFactory]);
  /**
  * Rate service constructor. rates a recipe based on user id and recipe id
  * @constructor
  */
function RateServiceFactory($http,apiRateUrl){
  return function(recipeId, rating, userId, successCallback, errorCallback){
    $http({
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      url: (apiRateUrl + recipeId + '/rate/' + rating),
      data:{
        userID:userId
      }
    }).then(successCallback, errorCallback);
  };
}

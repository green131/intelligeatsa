'use strict';

angular.module('intelligeatsa.services')
.constant('apiRateUrl','http://localhost:8080/recipe/')
.factory('rate',['$http', 'apiRateUrl', RateServiceFactory]);
  /**
  * Rate service constructor. rates a recipe based on user id and recipe@param {SearchQuery} searchQuery - object with search query info
  * @constructor 
  * @param {string} recipeId - id for recipe
  * @param {string} rating - rating value
  * @param {string} userId - user id
  * @param {Function} successCallback - callback on rate success
  * @param {Function} errorCallback - callback on rate error
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

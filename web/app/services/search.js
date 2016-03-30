'use strict';

angular.module('intelligeatsa.services')
.constant('apiSearchUrl','http://localhost:8080/recipe/search')
.factory('search',['$http', 'apiSearchUrl', SearchServiceFactory]);

function SearchServiceFactory($http,apiSearchUrl){
  /**
  * User session service, creates session based on login or registration
  * @constructor
  */
  var searchService = function(){
    var service = this;
    var request = function(searchQueryInfo,start,end,successCallback,errorCallback){
      console.log(searchQueryInfo.searchType);
      if(searchQueryInfo.searchType === 'tags'){
        $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        data:{
          tags:[searchQueryInfo.query]
        },
        url: (apiSearchUrl + '/' + start + '/' + end)
      }).then(function success(response){
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
      }else
      if(searchQueryInfo.searchType === 'ingredients'){
         $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        data:{
          ingredients:searchQueryInfo.query.split(',')
        },
        url: (apiSearchUrl + '/' + start + '/' + end)
      }).then(function success(response){
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
      }
      else
      if(searchQueryInfo.searchType === 'prepTime'){
         $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        data:{
          prepTime:searchQueryInfo.query
        },
        url: (apiSearchUrl + '/' + start + '/' + end)
      }).then(function success(response){
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
      }
    };


    /**
    * Query search
    * @function
    * @param {SearchQueryO} searchQuery - object with search query info
    * @param {string} batchSize - size of each batch of results
    * @returns {Paginator} - paginator object
    */

    this.query = function(searchQueryInfo,batchSize){
      /**
      * Paginator instance
      * @constructor
      * @memberof searchService
      */
      var Paginator = function(){
        var start = -1 * batchSize;
        var end = 0;
        /**
        * Gets the next batch of results based on search query
        * @function
        * @param {Function} successCallback - callback once next batch received
        * @param {Function} errorCallback - callback for error
        */
        this.next = function(successCallback, errorCallback){
          start += batchSize;
          end += batchSize;
          request(searchQueryInfo, start, end, successCallback, errorCallback);
        };
        /**
        * Gets the previous batch of results based on search query
        * @function
        * @param {Function} successCallback - callback once next batch received
        * @param {Function} errorCallback - callback for error
        */
        this.previous = function(successCallback, errorCallback){
          if(start > 0){
            start -= batchSize;
            end -= batchSize;
          }
          request(searchQueryInfo, start, end, successCallback, errorCallback);
        };
      };
      return new Paginator();
    };

  };
  return new searchService();
}

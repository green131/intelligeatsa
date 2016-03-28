'use strict';

angular.module('intelligeatsa.services')
.constant('apiSearchUrl','http://localhost:8080/recipe/search/')
.factory('search',['$http', 'apiSearchUrl', SearchServiceFactory]);

function SearchServiceFactory($http,apiSearchUrl){
  /**
  * User session service, creates session based on login or registration
  * @constructor
  */
  var searchService = function(){
    var service = this;
    var request = function(searchQuery,start,end,successCallback,errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: (apiSearchUrl + searchQuery + '/' + start + '/' + end)
      }).then(function success(response){
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
    };


    /**
    * Creates a session from email registration
    * @function
    * @param {string} searchQuery - query to search
    * @param {string} batchSize - size of each batch of results
    * @returns {Paginator} - paginator object
    */

    this.query = function(searchQuery,batchSize){
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
          request(searchQuery, start, end, successCallback, errorCallback);
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
          request(searchQuery, start, end, successCallback, errorCallback);
        };
      };
      return new Paginator();
    };

  };
  return new searchService();
}

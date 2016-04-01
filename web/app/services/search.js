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
      console.log(searchQueryInfo);
      var postData = {};
      if(searchQueryInfo.searchType === 'tags'){
        var tags = searchQueryInfo.query.split(',');
        for(var i=0;i<tags.length;i++){
          tags[i] = tags[i].trim();
        }
        postData.tags = tags;
        if(searchQueryInfo.hasOwnProperty('sort'))
          postData.sort = searchQueryInfo.sort;
      }else
      if(searchQueryInfo.searchType === 'ingredients'){
         postData.ingredients = searchQueryInfo.query.split(',');
         if(searchQueryInfo.hasOwnProperty('sort'))
          postData.sort = searchQueryInfo.sort;
      }
      else
      if(searchQueryInfo.searchType === 'prepTime'){
        postData.prepTime = searchQueryInfo.query;
        postData.sort = 'prepR';
      }

       $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        data:postData,
        url: (apiSearchUrl + '/' + start + '/' + end)
      }).then(function success(response){
        successCallback(response.data);
      }, function error(response){
        errorCallback(response);
      });
    };


    /**
    * Query search
    * @function
    * @param {SearchQuery} searchQuery - object with search query info
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

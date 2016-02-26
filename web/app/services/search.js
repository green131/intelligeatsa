'use strict';

angular.module('intelligeatsa.services')
.constant('apiSearchUrl','http://localhost:8080/recipe/search/')
.factory('search',['$http', 'apiSearchUrl', SearchServiceFactory]);

function SearchServiceFactory($http,apiSearchUrl){
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
    // public api
    this.query = function(searchQuery,batchSize){
      // new paginator instance.
      var Paginator = function(){
        var start = -1 * batchSize;
        var end = 0;
        this.next = function(successCallback, errorCallback){
          start += batchSize;
          end += batchSize;
          request(searchQuery, start, end, successCallback, errorCallback);
        };

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

angular.module('intelligeatsa.services')
.factory('searchLog',SearchLogServiceFactory);

function SearchLogServiceFactory(){
  var searchLogService = function(){
    var log = [];
    this.push = function(query){
      log.push(query);
    };

    this.getLog = function(){
      return log;
    };
  };
  return new searchLogService();
}

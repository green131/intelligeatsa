angular.module('intelligeatsa.services')
.factory('mongoUtils',MongoUtilsServiceFactory);

function MongoUtilsServiceFactory(){
  var mongoUtilsService = function(){
    this.mongoIdObjToString = function(id){
      var timestamp = id.timestamp.toString(16);
      var machine = id.machineIdentifier.toString(16);
      var pid = id.processIdentifier.toString(16);
      var counter = id.counter.toString(16);
      return timestamp + machine + pid + counter;
    };
  };
  return new mongoUtilsService();
};

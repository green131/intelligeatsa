angular.module('intelligeatsa.services')
.factory('mongoUtils',MongoUtilsServiceFactory);

function MongoUtilsServiceFactory(){
  /**
  * mongo utility service, utilities for mongo
  * @constructor
  */
  var mongoUtilsService = function(){
    /**
    * calculate hex string from mongo id object. timestamp + machine + pid + counter
    * @function
    * @param {function} id - mongo id object
    */
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

'use strict';

angular.module('intelligeatsa.services')
.constant('apiRegistrationUrl','http://localhost:8080/user/register')
.constant('apiLoginUrl','http://localhost:8080/user/login')
.factory('userSession',['$http','$rootScope','SESSION_EVENTS','apiRegistrationUrl','apiLoginUrl',UserServiceFactory]);

function UserServiceFactory($http,$rootScope,SESSION_EVENTS,apiRegistrationUrl,apiLoginUrl){

  var userSessionService = function(){
    var service = this;

    // private variables
    var user = null;

    var broadcast = function(eventName){
      $rootScope.$broadcast(eventName);
    };

    // public
    this.createSessionFromRegistration = function(email,password,successCallback,errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiRegistrationUrl,
        data:{
          'user': email,
          'pass': password
        }
      }).then(function (response) {
        user = response.data;
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

    this.createSessionFromExistingUser = function(email,password,successCallback,errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiLoginUrl,
        data:{
          'user': email,
          'pass': password
        }
      }).then(function(response) {
        user = response.data;
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function(response) {
        errorCallback(response);
      });
    };

    this.getUser = function(){
      return user;
    };

    this.sessionExists = function(){
      return (user != null);
    };

    this.closeSession = function(){
      user = null;
      broadcast(SESSION_EVENTS.SESSION_CLOSED);
    };
  };

  return new userSessionService();
}

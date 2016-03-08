'use strict';

angular.module('intelligeatsa.services')
.constant('apiRegistrationUrl','http://localhost:8080/user/register')
.constant('apiLoginUrl','http://localhost:8080/user/login')
.constant('SESSION_EVENTS',{
  SESSION_CREATED: 'SESSION_CREATED',
  SESSION_CLOSED: 'SESSION_CLOSED'
})
.factory('userSession',['$http','$rootScope','SESSION_EVENTS','apiRegistrationUrl','apiLoginUrl',UserSessionServiceFactory]);

function UserSessionServiceFactory($http,$rootScope,SESSION_EVENTS,apiRegistrationUrl,apiLoginUrl){

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
          'pass': btoa(password)
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'email';
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
          'pass': btoa(password)
        }
      }).then(function(response) {
        user = response.data;
        user.sessionType = 'email';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function(response) {
        errorCallback(response);
      });
    };

    this.createSessionFromFacebookRegistration = function(name,userId,accessToken,successCallback,errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiRegistrationUrl,
        data:{
          'facebookName':name,
          'facebookUserId': userId,
          'facebookAccessToken': accessToken
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = facebook;
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

    this.createSessionFromGoogleRegistration = function(name, userId, accessToken, successCallback, errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiRegistrationUrl,
        data:{
          'googleName':name,
          'googleUserId': userId,
          'googleAccessToken': accessToken
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'google';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        user = {
          sessionType:'google'
        };
        errorCallback(response);
      });
    }

    this.getUser = function(){
      return user;
    };

    this.sessionExists = function(){
      return (user != null);
    };

    this.closeSession = function(){
      if(user.sessionType == 'google'){
        gapi.auth.signOut();
        console.log('signed out');
      }else if(user.sessionType == 'facebook'){

      }
      user = null;
      broadcast(SESSION_EVENTS.SESSION_CLOSED);
    };
  };

  return new userSessionService();
}

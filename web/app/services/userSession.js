'use strict';

angular.module('intelligeatsa.services')
.constant('apiRegistrationUrl','http://localhost:8080/user/register')
.constant('apiLoginUrl','http://localhost:8080/user/login')
.constant('apiLinkAccountsUrl','http://localhost:8080/user/linkUserAccount')
.constant('SESSION_EVENTS',{
  SESSION_CREATED: 'SESSION_CREATED',
  SESSION_CLOSED: 'SESSION_CLOSED'
})
.factory('userSession',['$http','$rootScope','SESSION_EVENTS','apiRegistrationUrl','apiLoginUrl','apiLinkAccountsUrl','ezfb','$window',UserSessionServiceFactory]);

function UserSessionServiceFactory($http,$rootScope,SESSION_EVENTS,apiRegistrationUrl,apiLoginUrl,apiLinkAccountsUrl,ezfb,$window){

  /**
  * User session service, creates session based on login or registration
  * @constructor
  */
  var userSessionService = function(){

    /** @access private */
    var service = this;

    /** @access private */
    var user = null;

    var broadcast = function(eventName){
      $rootScope.$broadcast(eventName);
    };

    /**
    * Creates a session from email registration
    * @function
    * @param {string} email - user specified email
    * @param {string} password - password
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    * @fires SESSION_EVENTS.SESSION_CREATED
    */
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

    /**
    * Creates a session from email login
    * @function
    * @param {string} email - user specified email
    * @param {string} password - password
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */
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
    /**
    * Creates a session from registration using Facebook auth
    * @function
    * @param {string} name - Facebook name of user
    * @param {string} fbUserId - Facebook user id
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */
    this.createSessionFromFacebookRegistration = function(name,fbUserId,successCallback,errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiRegistrationUrl,
        data:{
          'user':name,
          'fbId': fbUserId
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'facebook';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

    /**
    * Creates a session from login using facebook auth
    * @function
    * @param {string} name - facebook name of user
    * @param {string} fbUserId - facebook user id
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */

    this.createSessionFromFacebookLogin = function(name, fbUserId, successCallback, errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiLoginUrl,
        data:{
          'user':name,
          'fbId': fbUserId
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'facebook';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

    /**
    * Creates a session from registration using Google Auth
    * @function
    * @param {string} name - Google name of user
    * @param {string} googleUserId - Google user id
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */
    this.createSessionFromGoogleRegistration = function(name, googleUserId, successCallback, errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiRegistrationUrl,
        data:{
          'user':name,
          'googleId': googleUserId
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'google';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };
     /**
    * Creates a session from login using Google Auth
    * @function
    * @param {string} name - Google name of user
    * @param {string} googleUserId - Google user id
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */

    this.createSessionFromGoogleLogin = function(name, googleUserId, successCallback, errorCallback){
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiLoginUrl,
        data:{
          'user':name,
          'googleId': googleUserId
        }
      }).then(function (response) {
        user = response.data;
        user.sessionType = 'google';
        broadcast(SESSION_EVENTS.SESSION_CREATED);
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

     /**
    * Links a social account to the current user
    * @function
    * @param {string} socialAccountType - fbId or googleId
    * @param {string} socialId - social account user id
    * @param {function} successCallback - callback after successful registration
    * @param {function} errorCallback - callback after error in registration
    */

    this.linkSocialAccount = function(socialAccountType, socialId, successCallback, errorCallback){
      if(!user){
        return errorCallback('no user');
      }
      var postData = {};
      postData[socialAccountType] = socialId;
      postData['token'] = user.token;
      console.log('linking accounts', socialAccountType);
      $http({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        url: apiLinkAccountsUrl,
        data:postData
      }).then(function (response) {
        successCallback();
      }, function (response) {
        errorCallback(response);
      });
    };

    /**
    * Gets the user in current session
    * @function
    * @returns {User} user object
    */
    this.getUser = function(){
      return user;
    };

    /**
    * Checks to see if a session currently exists
    * @function
    * @returns {Boolean} whether the session exists or not
    */
    this.sessionExists = function(){
      return (user != null);
    };

    /**
    * Closes the current session and logouts of third party session if any exist.
    * @function
    */
    this.closeSession = function(){
      if(user.sessionType == 'google'){
        window.googleAuth.signOut();
        console.log('logged out Google')
      }else if(user.sessionType == 'facebook'){
        ezfb.logout();
        console.log('logged out Facebook');
      }
      user = null;
      broadcast(SESSION_EVENTS.SESSION_CLOSED);
      // redirect to home page
      $window.location.href= '#/';
    };
  };

  return new userSessionService();
}

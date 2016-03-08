'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyRegisterForm',{
  templateUrl:'components/shared/thirdPartyRegisterForm/thirdPartyRegisterFormTemplate.html',
  controller:ThirdPartyRegisterFormController
});

function ThirdPartyRegisterFormController($http,$scope,userSession,ezfb){
  var ctrl = this;
  ctrl.registerFB = function(){
    ezfb.login(function(res){
      if(res.authResponse){
        createFBSession(res.authResponse.accessToken, res.authResponse.userID);
      }else{

      }
    });
  };

  // authentication handlers
  var createFBSession = function(accessToken,userID){
    // get the name of the user
    ezfb.api('/me',function(res){
      if(res.hasOwnProperty('name')){
        userSession.createSessionFromFacebookRegistration(res.name,userID,accessToken,function success(){

        },function error(){
        })
      }
    });
  }

  $scope.$on('event:google-plus-signin-success', function (event,authResult) {
    gapi.client.load('plus', 'v1', function(){
      gapi.client.plus.people.get({userId: 'me'}).execute(function(res){
        console.log(res.displayName);
        console.log(res.id)
        userSession.createSessionFromGoogleRegistration(res.displayName, res.id, authResult.accessToken,
          function success(){
          }, function error(){
            console.log('logout');
              userSession.closeSession();
          });
        });
      });
    });

    $scope.$on('event:google-plus-signin-failure', function (event,authResult) {
      // Auth failure or signout detected
    });
  }

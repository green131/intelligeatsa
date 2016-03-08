'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyRegisterForm',{
  templateUrl:'components/shared/thirdPartyRegisterForm/thirdPartyRegisterFormTemplate.html',
  controller:ThirdPartyRegisterFormController
});

function ThirdPartyRegisterFormController($http,userSession,ezfb,$auth){
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

    var createGoogleSession = function(){

    };

    var ctrl = this;
    ctrl.registerFB = function(){
      ezfb.login(function(res){
        if(res.authResponse){
          createFBSession(res.authResponse.accessToken, res.authResponse.userID);
        }else{

        }
      });
    };
    ctrl.registerGoogle = function(){
      $auth.authenticate('google');
    };
  }

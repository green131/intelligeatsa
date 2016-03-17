'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyRegisterForm',{
  templateUrl:'components/shared/thirdPartyRegisterForm/thirdPartyRegisterFormTemplate.html',
  controller:ThirdPartyRegisterFormController
});

function ThirdPartyRegisterFormController($http,$scope,userSession,ezfb){
  var ctrl = this;
  var registeringGoogle = false;

  ctrl.registerFB = function(){
    ezfb.login(function(res){
      if(res.authResponse){
        var userId = res.authResponse.userID;
        ezfb.api('/me',function(res){
          if(res.hasOwnProperty('name')){
            userSession.createSessionFromFacebookRegistration(res.name,userId,function success(){
              $('#loginModal').modal('hide');
            },function error(err){
              console.log(err);
            })
          }
        });
      }else{

      }
    });
  };

  ctrl.registerGoogle = function(){
    window.googleAuth.signIn().then(function(googleUser){
      var profile = googleUser.getBasicProfile();
      var id = profile.getId();
      var name = profile.getName();
      userSession.createSessionFromGoogleRegistration(name, id,
        function success(){
          $('#loginModal').modal('hide');
          console.log('registered with with google');
        }, function error(){
          userSession.closeSession();
        });
      });
    };
}

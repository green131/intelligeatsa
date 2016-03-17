'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyLoginForm',{
  templateUrl:'components/shared/thirdPartyLoginForm/thirdPartyLoginFormTemplate.html',
  controller:ThirdPartyLoginFormController
});

function ThirdPartyLoginFormController($http,userSession,ezfb,$scope){
  var ctrl = this;
  var loginWithGoogle = false;

  ctrl.loginFB = function(){
    ezfb.login(function(res){
      if(res.authResponse){
        var userId = res.authResponse.userID;
        ezfb.api('/me',function(res){
          if(res.hasOwnProperty('name')){
            userSession.createSessionFromFacebookLogin(res.name,userId,function success(){
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

  ctrl.loginGoogle = function(){
    window.googleAuth.signIn().then(function(googleUser){
      var profile = googleUser.getBasicProfile();
      var id = profile.getId();
      var name = profile.getName();
      userSession.createSessionFromGoogleLogin(name, id,
        function success(){
          $('#loginModal').modal('hide');
          console.log('registered with with google');
        }, function error(){
          userSession.closeSession();
        });
      });
    };
}

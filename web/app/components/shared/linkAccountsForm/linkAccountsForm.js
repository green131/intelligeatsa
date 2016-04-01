'use strict';

angular.module('intelligeatsa.components')
.component('linkAccountsForm',{
  templateUrl:'components/shared/linkAccountsForm/linkAccountsFormTemplate.html',
  controller:LinkAccountsFormController
});

function LinkAccountsFormController($http, userSession, ezfb){
  var ctrl = this;
  ctrl.linkFacebookAccount = function(){
    ezfb.login(function(res){
      if(res.authResponse){
        var userId = res.authResponse.userID;
        ezfb.api('/me',function(res){
          if(res.hasOwnProperty('name')){
            userSession.linkSocialAccount('fbId',userId,function success(){
              $('#linkAccountsFormModal').modal('hide');
              alert('Facebook account was successfully linked!');
              console.log('linked with facebook');
            },function error(err){
              $('#linkAccountsFormModal').modal('hide');
              alert('You already linked a Facebook account.');

              console.log(err);
            });
          }
        });
      }else{

      }
    });
  };

  ctrl.linkGoogleAccount = function(){
    window.googleAuth.signIn().then(function(googleUser){
      var profile = googleUser.getBasicProfile();
      var id = profile.getId();
      userSession.linkSocialAccount('googleId',id,function success(){
        $('#linkAccountsFormModal').modal('hide');
        alert('Google account was successfully linked!');
        console.log('linked with google');
      },function error(err){
        $('#linkAccountsFormModal').modal('hide');
        alert('You already linked a google account.');
        console.log(err);
      });
      });
    };
  }

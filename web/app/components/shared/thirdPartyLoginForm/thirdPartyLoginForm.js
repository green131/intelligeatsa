'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyLoginForm',{
  templateUrl:'components/shared/thirdPartyLoginForm/thirdPartyLoginFormTemplate.html',
  controller:ThirdPartyLoginFormController
});

function ThirdPartyLoginFormController($http,userSession,ezfb,$scope){
  var ctrl = this;


  // google authentication handler
  $scope.$on('event:google-plus-signin-success', function (event,authResult) {
    gapi.client.load('plus', 'v1', function(){
      gapi.client.plus.people.get({userId: 'me'}).execute(function(res){
        console.log(res.displayName);
        console.log(res.id)
        // create session from existing google user.
        });
      });
    });

    $scope.$on('event:google-plus-signin-failure', function (event,authResult) {
      // Auth failure or signout detected
    });
}

'use strict';

angular.module('intelligeatsa.components')
.component('loginForm',{
  templateUrl:'public/components/shared/loginForm/loginFormTemplate.html',
  controller:LoginFormController,
  bindings:{
    placeholderEmail: '@',
    placeholderPassword: '@'
  }
});


function LoginFormController($http, userSession){
  var ctrl = this;
  ctrl.authenticate = function(){
    userSession.createSessionFromExistingUser(ctrl.email,ctrl.password,function success(){
      console.log(userSession.getUser());
      $('#loginModal').modal('hide');
    },function error(){
      console.log('error');
    });
  };
}

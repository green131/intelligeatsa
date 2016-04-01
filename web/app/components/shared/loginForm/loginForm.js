'use strict';

angular.module('intelligeatsa.components')
.component('loginForm',{
  templateUrl:'components/shared/loginForm/loginFormTemplate.html',
  controller:LoginFormController,
  bindings:{
    placeholderEmail: '@',
    placeholderPassword: '@'
  }
});

function LoginFormController($http, userSession){
  var ctrl = this;
  ctrl.authenticate = function(){
    if(ctrl.email.trim() == '' || ctrl.password.trim() == '' || ctrl.email.indexOf('@') == -1 || ctrl.email.indexOf('.') == -1){
      alert('Invalid email or password input for login.');
      return;
    }
    userSession.createSessionFromExistingUser(ctrl.email,ctrl.password,function success(){
      console.log(userSession.getUser());
      $('#loginModal').modal('hide');
    },function error(){
      alert('User does not exist!');
    });
  };
}

'use strict';

angular.module('intelligeatsa.components')
.component('registerForm',{
  templateUrl:'components/shared/registerForm/registerFormTemplate.html',
  controller:RegisterFormController,
  bindings:{
    placeholderEmail: '@',
    placeholderPassword: '@'
  }
});

function RegisterFormController($http,userSession){
  var ctrl = this;
  ctrl.register = function(){
    if(ctrl.email.trim() == '' || ctrl.password.trim() == '' || ctrl.email.indexOf('@') == -1 || ctrl.email.indexOf('.') == -1){
      alert('Invalid email or password input for registration.');
      return;
    }
    userSession.createSessionFromRegistration(ctrl.email,ctrl.password,function success(){
      $('#loginModal').modal('hide');
    }, function error(){
      alert('Email already taken!');
    });
  };
}

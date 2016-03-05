'use strict';

angular.module('intelligeatsa.components')
.component('registerForm',{
  templateUrl:'public/components/shared/registerForm/registerFormTemplate.html',
  controller:RegisterFormController,
  bindings:{
    placeholderEmail: '@',
    placeholderPassword: '@'
  }
});

function RegisterFormController($http,userSession){
  var ctrl = this;
  ctrl.register = function(){
    userSession.createSessionFromRegistration(ctrl.email,ctrl.password,function success(){
      $('#loginModal').modal('hide');
    }, function error(){

    });
  };
}

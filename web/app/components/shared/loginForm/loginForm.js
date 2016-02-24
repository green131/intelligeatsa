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


function LoginFormController($http){
  var ctrl = this;
  ctrl.loginApiUrl = 'http://localhost:8080/user/login';
  ctrl.userToken = null;
  ctrl.authenticate = function(){
    $http({
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    url: ctrl.loginApiUrl,
    data:{
      'user': ctrl.email,
      'pass': ctrl.password
    }
  }).then(function successCallback(response) {
      ctrl.userToken = response.data;
    }, function errorCallback(response) {
      console.log(response);
    });
  };
}

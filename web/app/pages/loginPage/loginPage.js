'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login',{
    templateUrl: 'pages/loginPage/loginPage.html',
     controller: LoginPageController,
     controllerAs: '$ctrl'
  });
}]);

function LoginPageController($http){
  var ctrl = this;
  ctrl.emailAddress = '';
  ctrl.password = '';


  ctrl.login = function(){
    console.log("hello");
    console.log(ctrl.emailAddress);
    console.log(ctrl.password);
  };
}

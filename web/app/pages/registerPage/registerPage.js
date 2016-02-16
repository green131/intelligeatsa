'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/register',{
    templateUrl: 'pages/registerPage/registerPage.html',
     controller: RegisterPageController,
     controllerAs: '$ctrl'
  });
}]);

function RegisterPageController($http){
  var ctrl = this;
  ctrl.emailAddress = '';
  ctrl.password = '';


  ctrl.register = function(){
    console.log("hello");
    console.log(ctrl.emailAddress);
    console.log(ctrl.password);
  };
}

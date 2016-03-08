'use strict';

// Declare root level module and dependencies
angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.services',
  'intelligeatsa.components',
  'intelligeatsa.pages',
  'ezfb',
  'satellizer'
]).config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}]).config(['ezfbProvider',function(ezfbProvider){
  ezfbProvider.setInitParams({
    appId: '658517684288010',
    version: 'v2.5'
  });
}]).config(['$authProvider', function($authProvider) {
  $authProvider.google({
    clientId: '790030169806-2os08flcrvpqo4iql2b3slborg5jmuds.apps.googleusercontent.com',
    responseType: 'token',
    redirectUri: 'http://localhost:9000/'
  });
}]);

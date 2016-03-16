'use strict';

// Declare root level module and dependencies
angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.services',
  'intelligeatsa.components',
  'intelligeatsa.pages',
  'ezfb',
  'directive.g+signin'
]).config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}]).config(['ezfbProvider',function(ezfbProvider){
  ezfbProvider.setInitParams({
    appId: '658517684288010',
    version: 'v2.5'
  });
}]);

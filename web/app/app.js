'use strict';

// Declare root level module which depends components
var rootModule = angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.components',
  'intelligeatsa.containers'
]);

rootModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}]);

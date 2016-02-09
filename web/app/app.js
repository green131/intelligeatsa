'use strict';

// Declare root level module and dependencies
var rootModule = angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.components',
  'intelligeatsa.pages'
]);

rootModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}]);

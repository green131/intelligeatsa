'use strict';

// Declare app level module which depends components
var rootModule = angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.header',
]);

rootModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}]);

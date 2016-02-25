'use strict';

// Declare root level module and dependencies
angular.module('intelligeatsa', [
  'ngRoute',
  'intelligeatsa.services',
  'intelligeatsa.components',
  'intelligeatsa.pages'
]).config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/home'});
}])
// root constants
.constant('SESSION_EVENTS',{
  SESSION_CREATED: 'SESSION_CREATED',
  SESSION_CLOSED: 'SESSION_CLOSED'
});

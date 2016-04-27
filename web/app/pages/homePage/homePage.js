'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/home',{
    templateUrl: 'pages/homePage/homePage.html',
    controller: HomePageController,
    controllerAs: '$ctrl'
  });
}]);

function HomePageController($http,$rootScope,SESSION_EVENTS, userSession){
  var ctrl = this;
  ctrl.sessionExists = userSession.sessionExists();

  $rootScope.$on(SESSION_EVENTS.SESSION_CREATED,function(){
    ctrl.sessionExists = true;
    ctrl.user = userSession.getUser();
  });

  $rootScope.$on(SESSION_EVENTS.SESSION_CLOSED,function(){
    ctrl.sessionExists = false;
    ctrl.user = null;
  });

  ctrl.now = moment();
  ctrl.Weekday = ctrl.now.isoWeekday();
  
}

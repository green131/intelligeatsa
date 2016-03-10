'use strict';

angular.module('intelligeatsa.components')
.component('header',{
  templateUrl:'components/shared/header/headerTemplate.html',
  controller:HeaderController,
  bindings:{
  }
});

function HeaderController($http,$rootScope,userSession,SESSION_EVENTS){
  var ctrl = this;
  ctrl.sessionExists = false;
  if(userSession.sessionExists()){
    ctrl.user = userSession.getUser();
    ctrl.sessionExists = true;
  }else{
    ctrl.user = null;
    ctrl.sessionExists = false;
  }
  // subscribe to session events
  $rootScope.$on(SESSION_EVENTS.SESSION_CREATED,function(){
    ctrl.sessionExists = true;
    ctrl.user = userSession.getUser();
  });

  $rootScope.$on(SESSION_EVENTS.SESSION_CLOSED,function(){
    ctrl.sessionExists = false;
    ctrl.user = null;
  });

  ctrl.logout = function(){
    userSession.closeSession();
  };

  ctrl.showLinkAccountsFormModal = function(){
    $('#linkAccountsFormModal').modal('toggle');
  };
}

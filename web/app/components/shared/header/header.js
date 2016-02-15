'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('header',{
  templateUrl:'components/shared/header/headerTemplate.html',
  controller:HeaderController,
  bindings:{
  }
});

function HeaderController($http){
  var ctrl = this;

  this.login = function(){
    console.log('going to login');
  }

}

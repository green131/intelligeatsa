'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('header',{
  templateUrl:'components/shared/header/headerTemplate.html',
  controller:HeaderController
});

function HeaderController($http){
  console.log($http);
  var ctrl = this;
  ctrl.title = 'Intelligeatsa';

}

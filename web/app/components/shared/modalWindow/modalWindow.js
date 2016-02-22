'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('modalWindow',{
  templateUrl:'components/shared/modalWindow/modalWindowTemplate.html',
  controller:ModalWindowController,
  transclude:true,
  bindings:{
    'modalId':'@'
  }
});

function ModalWindowController($http){
  var ctrl = this;
  ctrl.login = function(){
    console.log('going to login');
  }
  console.log(ctrl.modalId + ' = modalId');
}

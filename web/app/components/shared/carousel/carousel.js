'use strict';

var componentsModule = angular.module('intelligeatsa.components');

componentsModule.component('carousel',{
  templateUrl:'components/shared/carousel/carouselTemplate.html',
  controller:CarouselController,
  bindings:{
    title:'@'
  }
});


function CarouselController(){
  var ctrl = this;
}

'use strict';

angular.module('intelligeatsa.components')
.component('thirdPartyLoginForm',{
  templateUrl:'components/shared/thirdPartyLoginForm/thirdPartyLoginFormTemplate.html',
  controller:ThirdPartyLoginFormController
});

function ThirdPartyLoginFormController($http,userSession,ezfb){
  var ctrl = this;
}

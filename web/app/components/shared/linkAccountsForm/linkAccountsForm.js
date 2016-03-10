'use strict';

angular.module('intelligeatsa.components')
.component('linkAccountsForm',{
  templateUrl:'components/shared/linkAccountsForm/linkAccountsFormTemplate.html',
  controller:LinkAccountsFormController
});

function LinkAccountsFormController($http, userSession){
  var ctrl = this;

}

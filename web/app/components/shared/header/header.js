var componentsModule = app.module('intelligeatsa.header');
headerModule.component('header',{
  templateUrl:'headerTemplate.html',
  controller:HeaderController
});

function HeaderController($scope, $element, $attrs){
  var ctrl = this;
  ctrl.title = 'Intelligeatsa';

}

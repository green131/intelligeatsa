'use strict';

angular.module('intelligeatsa.pages')
.constant('apiRateUrl','http://localhost:8080/recipe/')
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/recipe/:id',{
    templateUrl: 'pages/recipePage/recipePage.html',
    controller: RecipePageController,
    controllerAs: '$ctrl'
  });
}]);


function RecipePageController($http, $routeParams, $rootScope, groceryList, SESSION_EVENTS, userSession){
  var ctrl = this;
  var recipeId = $routeParams.id;
  var recipeUrl = "http://localhost:8080/recipe/id/" + recipeId;
  ctrl.recipe = '';
  ctrl.instructionList=[];
  ctrl.inGroceryList = false;
  ctrl.sessionExists = false;
  ctrl.user = null;

  function init(){
    if(userSession.sessionExists()){
      ctrl.user = userSession.getUser();
      ctrl.sessionExists = true;
      groceryList.contains(recipeId, function success(resultBool){
        ctrl.inGroceryList = resultBool;
      }, function error(){
        $('#loginModal').modal('show');
      });
    }
  }

  ctrl.addToGroceryList = function(){
    groceryList.add(recipeId, function success(){
      ctrl.inGroceryList = true;
    }, function error(){
      $('#loginModal').modal('show');
    });
  };

  ctrl.removeFromGroceryList = function(){
    groceryList.remove(recipeId, function success(){
      ctrl.inGroceryList = false;
    }, function error(){
      $('#loginModal').modal('show');
    });
  };

  $http({
    method: 'POST',
    url: recipeUrl,
  }).then(function successCallback(response) {
      ctrl.recipe = response.data;
      ctrl.instructionList = ctrl.recipe.preparation[0];
      ctrl.recipeId = ctrl.recipe._id.$oid;
    console.log(response);
  }, function errorCallback(response){
    console.log(response);
  });


  $rootScope.$on(SESSION_EVENTS.SESSION_CREATED,function(){
    ctrl.sessionExists = true;
    ctrl.user = userSession.getUser();
    init();
  });

  $rootScope.$on(SESSION_EVENTS.SESSION_CLOSED,function(){
    ctrl.sessionExists = false;
    ctrl.user = null;
  });

  init();
}

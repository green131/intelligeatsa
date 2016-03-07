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

function RecipePageController($http, $routeParams,apiRateUrl,userSession){
  var ctrl = this;
  var recipeUrl = "http://localhost:8080/recipe/id/" + $routeParams.id;
  ctrl.recipe = '';
  ctrl.noRating = false;
  ctrl.instructionList=[];
  $http({
  method: 'POST',
  url: recipeUrl,
  }).then(function successCallback(response) {
      ctrl.recipe = response.data;
      ctrl.instructionList = ctrl.recipe.preparation[0];
      ctrl.recipeId = ctrl.recipe._id.$oid;
      // round
      if(ctrl.recipe.hasOwnProperty('rating')){
        ctrl.recipe.rating.value = Math.floor(ctrl.recipe.rating.value);
      }else{
        ctrl.noRating = true;
      }
  }, function errorCallback(response){
    console.log(response);
  });

  ctrl.rateRecipe = function(rating){
    if(!userSession.sessionExists()){
      $('#loginModal').modal('show');
      return;
    }
    var user = userSession.getUser();
    console.log(user);
    $http({
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      url: (apiRateUrl + ctrl.recipeId + '/rate/' + rating),
      data:{
        userID:user.token
      }
    }).then(function success(response){
      if(ctrl.noRating){
        ctrl.recipe.rating = {
          value: 0,
          numOfRaters:0
        };
        ctrl.noRating = false;
      }
      var originalSum = (ctrl.recipe.rating.value * ctrl.recipe.rating.numOfRaters);
      var updatedSum = originalSum + rating;
      ctrl.recipe.rating.numOfRaters++;
      var newRating = Math.floor(updatedSum / ctrl.recipe.rating.numOfRaters);
      ctrl.recipe.rating.value = newRating;
      console.log(response);
    }, function error(response){
      console.log(response);
    });
  };
}

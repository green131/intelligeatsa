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

function RecipePageController($http, $routeParams,apiRateUrl,userSession,rate){
  var ctrl = this;
  var recipeUrl = "http://localhost:8080/recipe/id/" + $routeParams.id;
  var previousRating = -1;
  ctrl.recipe = '';
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
      ctrl.recipe.rating = {
        value: 0,
        numOfRaters: 0
      };
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

    rate(ctrl.recipeId, rating, user.token, function(response){
      if(previousRating == -1){
        $('#rate_'+rating).removeClass('btn-default').addClass('btn-warning');
      }else{
        // subtract their previous vote
        // select their chosen rating button
        $('#rate_'+previousRating).removeClass('btn-warning').addClass('btn-default');
        $('#rate_'+rating).removeClass('btn-default').addClass('btn-warning');
        var originalSum = (ctrl.recipe.rating.value * ctrl.recipe.rating.numOfRaters);
        var removedPreviousRatingSum = originalSum - previousRating;
        ctrl.recipe.rating.numOfRaters--;
        if(ctrl.recipe.rating.numOfRaters > 0){
          ctrl.recipe.rating.value = Math.floor(removedPreviousRatingSum / ctrl.recipe.rating.numOfRaters);
        }else{
          ctrl.recipe.rating.value = 0;
        }
      }
      var originalSum = (ctrl.recipe.rating.value * ctrl.recipe.rating.numOfRaters);
      var updatedSum = originalSum + rating;
      ctrl.recipe.rating.numOfRaters++;
      var newRating = Math.floor(updatedSum / ctrl.recipe.rating.numOfRaters);
      ctrl.recipe.rating.value = newRating;
      // now this rating will be the previous rating for the next call.
      previousRating = rating;
      console.log(response);
    }, function(err){

    });

  };
}

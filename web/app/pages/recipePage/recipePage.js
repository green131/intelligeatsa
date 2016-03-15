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

<<<<<<< HEAD
function RecipePageController($http, $routeParams,apiRateUrl,userSession,rate){
  var ctrl = this;
  var recipeUrl = "http://localhost:8080/recipe/id/" + $routeParams.id;
  var previousRating = -1;
=======
function RecipePageController($http, $routeParams, $rootScope, groceryList, SESSION_EVENTS, userSession){
  var ctrl = this;
  var recipeId = $routeParams.id;
  var recipeUrl = "http://localhost:8080/recipe/id/" + recipeId;
>>>>>>> master
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
<<<<<<< HEAD
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
=======
    console.log(response);
>>>>>>> master
  }, function errorCallback(response){
    console.log(response);
  });

<<<<<<< HEAD
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
=======

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
>>>>>>> master
}

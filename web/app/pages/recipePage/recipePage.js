'use strict';

var pagesModule = angular.module('intelligeatsa.pages');

// attach to url route
pagesModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/recipe/:id',{
    templateUrl: 'pages/recipePage/recipePage.html',
    controller: RecipePageController,
    controllerAs: '$ctrl'
  });
}]);

function RecipePageController($http, $routeParams, $rootScope, $scope, groceryList, SESSION_EVENTS, userSession, rate){
  var ctrl = this;
  var recipeId = $routeParams.id;
  var recipeUrl = "http://localhost:8080/recipe/id/" + recipeId;
  /* rating*/
  ctrl.previousRating = 0;

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
      // check if user has rated this recipe
      if(ctrl.user.ratingList){
      var existingRatingInfo = ctrl.user.ratingList.find(function(ratingObj){
        return (ratingObj.recipeID === recipeId);
      });

      if(existingRatingInfo){
        ctrl.previousRating = existingRatingInfo.myRating;
        // set the correct rate button to be highlighted;
        console.log(ctrl.previousRating);
      }
      }
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

   var fetchRecipe = function(){
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
  };


  ctrl.rateRecipe = function(rating){
    if(!userSession.sessionExists()){
      $('#loginModal').modal('show');
      return;
    }
    var user = userSession.getUser();

    rate(ctrl.recipeId, rating, user.token, function(response){
      // get ratingList and update it.
      if(ctrl.user.ratingList == undefined){
        ctrl.user.ratingList = [];
      }
      var existingRatingInfo = ctrl.user.ratingList.find(function(ratingObj){
        return (ratingObj.recipeID === recipeId);
      });

      if(existingRatingInfo){
        existingRatingInfo.myRating = rating;
      }else{
        user.ratingList.push({
          recipeID:ctrl.recipeId,
          myRating:rating
        });
      }


      if(ctrl.previousRating == 0){
        $('#rate_'+rating).removeClass('btn-default').addClass('btn-warning');
      }else{
        // subtract their previous vote
        // select their chosen rating button
        $('#rate_'+ctrl.previousRating).removeClass('btn-warning').addClass('btn-default');
        $('#rate_'+rating).removeClass('btn-default').addClass('btn-warning');
        // now this rating will be the previous rating for the next call.
      }
      ctrl.previousRating = rating;
      fetchRecipe();
    }, function(err){

    });

  };

  $rootScope.$on(SESSION_EVENTS.SESSION_CREATED,function(){
    ctrl.sessionExists = true;
    ctrl.user = userSession.getUser();
    init();
  });

  $rootScope.$on(SESSION_EVENTS.SESSION_CLOSED,function(){
    ctrl.sessionExists = false;
    ctrl.user = null;
  });


  fetchRecipe();
  init();
}

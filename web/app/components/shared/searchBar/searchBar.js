'use strict';

angular.module('intelligeatsa.components')
.component('searchBar',{
  templateUrl:'components/shared/searchBar/searchBarTemplate.html',
  controller:SearchBarController,
  bindings:{
    placeholder: '@'
  }
});

function SearchBarController($window,$http){
  var ctrl = this;
  ctrl.search = function(){
    $window.location.href= '#/search/'+ctrl.searchQuery + '/tags';
  };
  ctrl.showIngredientSearchModal = function(){
    $('#ingredientSearchModal').modal('toggle');
  };
  ctrl.showPrepTimeSearchModal = function(){
    $('#prepTimeSearchModal').modal('toggle');
  };

  ctrl.searchByIngredients = function(){
    var ingredientsSelected = [];
    var ingredientCheckBoxContainers = $('.ingredient_checkbox');
    ingredientCheckBoxContainers.each(function(index){
      var currentCheckBoxContainer = ingredientCheckBoxContainers[index];
      if(currentCheckBoxContainer.firstElementChild.checked){
        ingredientsSelected.push(currentCheckBoxContainer.innerText);
      }
    });
    console.log(ingredientsSelected);
    // now pass these to search results page and it will call the appropriate api
    if(ingredientsSelected.length > 0){
      // close the window
      $('#ingredientSearchModal').modal('toggle');
      $window.location.href= '#/search/'+ingredientsSelected.join() + '/ingredients';
    }else{
      console.log('no ingredients selected');
    }
  };

  ctrl.searchByPrepTime = function(){
    var prepTimeQuery = $("#prepTimeSelectBox").val();
    $('#prepTimeSearchModal').modal('toggle');
      $window.location.href= '#/search/'+prepTimeQuery + '/prepTime';
  };

  // configure autocomplete
  var autoCompletePossibilities = [
    'apple',
    'banana',
    'cherry',
    'grapes',
    'mangoes',
    'melons',
    'orange',
    'peaches',
    'pears',
    'pineapple',
    'strawberry',
    'tomato',
    'chicken',
    'avocado',
    'egg',
    'white rice',
    'brown rice',
    'mango lassi',
    'chicken and rice',
    'tuna sandwich',
    'tuna',
    'salmon',
    'shrimp',
    'peanut',
    'pork',
    'onion',
    'thai',
    'pasta',
    'panner',
    'chicken tikka',
    'salsa',
    'sweet potato',
    'tofu',
    'lentils',
    'curry',
    'indian',
    'mexican',
    'vietnamese',
    'chinese',
    'pita',
    'turkey',
    'sandwich'
  ];

  ctrl.prepTimes = [
    5,10,15,20,25,30,35,40,45,50,55,60
  ];

  $('#searchBar').autocomplete({
    source: autoCompletePossibilities,
    minLength:1,
    select: function(event, ui){
      if(ui.item){
        ctrl.searchQuery = ui.item.value;
      }
    }
  });


}

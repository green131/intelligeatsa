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
  ctrl.searchQuery = '';
  ctrl.search = function(){
    if(ctrl.searchQuery.trim() == ''){
      alert('Type in something to search!');
    }else{
      $window.location.href= '#/search/'+ctrl.searchQuery + '/tags';
    }
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
      alert('Select some ingredients to search.');
      $('#ingredientSearchModal').modal('show');
    }
  };

  ctrl.searchByPrepTime = function(){
    var prepTimeQuery = $("#prepTimeSelectBox").val();
    $('#prepTimeSearchModal').modal('toggle');
      $window.location.href= '#/search/'+prepTimeQuery + '/prepTime';
  };

  // configure autocomplete
  var autoCompletePossibilities = [
    'Apple',
    'Banana',
    'Cherry',
    'Grapes',
    'Mangoes',
    'Melons',
    'Orange',
    'Peaches',
    'Pears',
    'Pineapple',
    'Strawberry',
    'Tomato',
    'Chicken',
    'Avocado',
    'Egg',
    'White rice',
    'Brown rice',
    'Mango lassi',
    'Chicken and rice',
    'Vegetarian, Gluten Free',
    'Vegetarian',
    'High Fiber',
    'Kosher',
    'Low Cholesterol',
    'Low Cholesterol, High Fiber',
    'Indian, Vegetarian',
    'Tuna sandwich',
    'Tuna',
    'Salmon',
    'Shrimp',
    'Peanut',
    'Pork',
    'Onion',
    'Thai',
    'Pasta',
    'Paneer',
    'Chicken tikka',
    'Salsa',
    'Sweet potato',
    'Tofu',
    'Lentils',
    'Curry',
    'Indian',
    'Mexican',
    'Vietnamese',
    'Chinese',
    'Pita',
    'Turkey',
    'Sandwich'
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

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
    }
  }
}

'use strict';

describe('carousel component tests', function() {
  // load module to get components
  beforeEach(module('intelligeatsa'));

  it('should assign title binding to title parameter', inject(function($rootScope, $componentController,$http) {
    var title = 'indian';
    var cuisineType = 'indian'
    var component = $componentController('carousel',
    { $scope: $rootScope.$new() },
    {title: title,cuisineType:cuisineType});
    expect(component.title).toBe(title);
    expect(component.cuisineType).toBe(cuisineType);
  }));

});

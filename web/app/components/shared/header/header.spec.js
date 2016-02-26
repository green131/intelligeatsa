'use strict';

describe('header component tests', function() {
  // load module to get components
  beforeEach(module('intelligeatsa'));

  it('should assign title binding to title parameter', inject(function($rootScope, $componentController) {
    var component = $componentController('header',
    { $scope: $rootScope.$new() },
    { title: 'Intelligeatsa' });
    expect(component.title).toBe('Intelligeatsa');
  }));

});

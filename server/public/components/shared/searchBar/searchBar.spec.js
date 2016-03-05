'use strict';

describe('login component tests', function() {
  // load module to get components
  beforeEach(module('intelligeatsa'));

  it('should assign binding for placeholder', inject(function($rootScope, $componentController,$http) {
    var placeholder = 'placeholder';
    var component = $componentController('searchBar',
    { $scope: $rootScope.$new() },
    {
      placeholder: placeholder
    });
      expect(component.placeholder).toBe(placeholder);
    }));

  });

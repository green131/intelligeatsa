'use strict';

describe('modal window component tests', function() {
  // load module to get components
  beforeEach(module('intelligeatsa'));

  it('should assign bindings', inject(function($rootScope, $componentController,$http) {
    var modalId = 'loginModal';
    var component = $componentController('modalWindow',
    { $scope: $rootScope.$new() },
    {
      modalId: modalId,
    });
      expect(component.modalId).toBe(modalId);
    }));

  });

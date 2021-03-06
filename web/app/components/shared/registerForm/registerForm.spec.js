'use strict';

describe('register form component tests', function() {
  // load module to get components
  beforeEach(module('intelligeatsa'));

  it('should assign binding for placeholderEmail and placeholderPassword', inject(function($rootScope, $componentController,$http) {
    var placeholderEmail = 'Email';
    var placeholderPassword = 'Password';
    var component = $componentController('registerForm',
    { $scope: $rootScope.$new() },
    {
      placeholderEmail: placeholderEmail,
      placeholderPassword:placeholderPassword
    });
      expect(component.placeholderEmail).toBe(placeholderEmail);
      expect(component.placeholderPassword).toBe(placeholderPassword);
    }));

  });

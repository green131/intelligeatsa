'use strict';
describe('recipe page', function ()){
  beforeEach(module('intelligeatsa.pages'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
      $httpBackend = _$httpBackend_;
      scope = $rootScope.$new();
      ctrl = $controller()
}   

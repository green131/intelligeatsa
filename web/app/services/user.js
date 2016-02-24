angular.module('intelligeatsa.services')
.factory('user',User);

function User(){
  return {
    setSession: function(userLoginInfo){
      this.userLoginInfo = userLoginInfo;
    },
    closeSession: function(){
      this.userLoginInfo = null;
    }
  };
};

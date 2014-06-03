'use strict';

agate.constant('USER_ROLES', {
  all: '*',
  admin: 'AGATE_ADMIN',
  user: 'AGATE_USER'
});

/* Services */

agate.factory('CurrentSession', ['$resource',
  function ($resource) {
    return $resource('ws/auth/session/_current');
  }]);

agate.factory('Account', ['$resource',
  function ($resource) {
    return $resource('ws/user/_current', {}, {
    });
  }]);

agate.factory('Password', ['$resource',
  function ($resource) {
    return $resource('ws/user/_current/password', {}, {
    });
  }]);

agate.factory('Session', ['$cookieStore',
  function ($cookieStore) {
    this.create = function (login, role) {
      this.login = login;
      this.role = role;
    };
    this.destroy = function () {
      this.login = null;
      this.role = null;
      $cookieStore.remove('account');
      $cookieStore.remove('agatesid');
      $cookieStore.remove('obibaid');
    };
    return this;
  }]);

agate.factory('AuthenticationSharedService', ['$rootScope', '$http', '$cookieStore', 'authService', 'Session', 'CurrentSession',
  function ($rootScope, $http, $cookieStore, authService, Session, CurrentSession) {
    return {
      login: function (param) {
        var data = 'username=' + param.username + '&password=' + param.password;
        $http.post('ws/auth/sessions', data, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          ignoreAuthModule: 'ignoreAuthModule'
        }).success(function () {
          CurrentSession.get(function (data) {
            Session.create(data.username, data.role);
            $cookieStore.put('account', JSON.stringify(Session));
            authService.loginConfirmed(data);
          });
        }).error(function () {
          Session.destroy();
        });
      },
      isAuthenticated: function () {
        if (!Session.login) {
          // check if the user has a cookie
          if ($cookieStore.get('account') !== null) {
            var account = JSON.parse($cookieStore.get('account'));
            Session.create(account.login, account.role);
            $rootScope.account = Session;
          }
        }
        return !!Session.login;
      },
      isAuthorized: function (authorizedRoles) {
        if (!angular.isArray(authorizedRoles)) {
          if (authorizedRoles === '*') {
            return true;
          }

          authorizedRoles = [authorizedRoles];
        }

        var isAuthorized = false;

        angular.forEach(authorizedRoles, function (authorizedRole) {
          var authorized = (!!Session.login &&
            Session.role === authorizedRole);

          if (authorized || authorizedRole === '*') {
            isAuthorized = true;
          }
        });

        return isAuthorized;
      },
      logout: function () {
        $rootScope.authenticationError = false;
        $http.delete('ws/auth/session/_current')
          .success(function () {
            Session.destroy();
            authService.loginCancelled();
          });
      }
    };
  }]);

angular.module('hello', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
    //$httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
    //$httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';
	
	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation'
	}).otherwise('/');



}).controller(
		'navigation',

		function($rootScope, $scope, $http, $location, $route) {
			$scope.greetingResult = {};
			
			$scope.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};
			
			
		    $scope.toggle = function() {
		        console.log('toggle');
		    }
		    
		    
			$scope.simpleRetrieve = function() {
				
				console.log('greeting');
				var username = 'daniels';
				var password = '123';
				
				// try with incorrect credentials like: var password = '1234';
				// restart server and open Chrome browser with incognito mode
				var credentials = btoa(username + ":" + password);
				var headers =  {
					Authorization : "Basic " + credentials
				};
				
				console.log('Credentials: ' + credentials);
				
				$http.get('greeting', {
					headers : headers
				}).success(function(data) {
					$scope.greetingResult = data;
					console.log('greeting ok. Result: ' + data)
				}).error(function(result) {
					$scope.greetingResult = 'Cannot retrieve data from greeting service - Unuthorized';
					console.log('greeting failure')
				});
				
				
			};		

			$scope.login2 = function(username, password) {
                var data = 'j_username=' + encodeURIComponent(username) +
                    '&j_password=' + encodeURIComponent(password) +
                    '&remember-me=' + false + '&submit=Login';
                console.log('Login in details: ' + data);
                return $http.post('api/authentication', data, {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (response) {
					$rootScope.authenticated = true;
					console.log('Login success');
					$location.path("/");
                    //return response;
                }).error(function() {
					$rootScope.authenticated = false;
					console.log('Login failed');
				});

            };
            
            $scope.newUser = {};
			$scope.addDefaultUser = function() {
                var data = {username : 'Pawel'}
                return $http.post('addUser', data).success(function (response) {
                	$scope.newUser = data;
					console.log('Default user added');
                }).error(function() {
					console.log('Cannot add user');
				});

            };            
            
            
			$scope.logout2 = function() {
				$http.post('api/logout', {}).success(function() {
					$rootScope.authenticated = false;
					console.log('Logout success');
					$location.path("/");
				}).error(function(data) {
					console.log('Logout failed')
					$rootScope.authenticated = false;
				});
			}
			
			
			var login3 = function(username, password) {

				var headers =  {
						Authorization : "Basic "
							+ btoa(username.username + ":"
									+ password)
				};

				$http.post('api/authentication', {
					headers : headers
				}).success(function(response) {
					console.log('success...');
				}).error(function() {
					console.log('failure...');
				});

			};			
			

		}).controller('home', function($scope, $http) {
	$http.get('/resource/').success(function(data) {
		$scope.greeting = data;
	})
});

var app = angular.module('companyApp', ['ngRoute'])

// define routes for the app, each route defines a template and a controller
app.config(['$routeProvider', function($routeProvider){
	$routeProvider
	.when('/', {
		templateUrl : './views/companies.html',
		controller  : 'CompaniesViewControl'
	})
	.when('/companies', {
		templateUrl : './views/companies.html',
		controller  : 'CompaniesViewControl'
	})
	.when('/companies/new', {
		templateUrl : './views/companyForm.html',
		controller  : 'CompanyViewControl'
	})
	.when('/companies/edit/:id', {
		templateUrl : './views/companyForm.html',
		controller  : 'CompanyViewControl'
	})
	.otherwise({
		redirectTo: '/'
	});
}]);

// this service will take care of keeping track of the companies and other
// operations
// for more on services see the documentation:
// https://docs.angularjs.org/guide/providers
// you can access a factory from the console by doing:
app.factory('Companies', function($http) {
	var service = {};

	service.entries = [];


	$http.get('companies').
	success(function(data){
		service.entries =  data;

		// convert date strings to Date objects
		// service.entries.forEach(function(element){
		// element.date = myHelpers.stringToDateObj(element.date);
		// });
	}).
	error(function(data, status){
		alert('error!');
	});

	// get an entry by id, using underscore.js
	service.getById = function(id) {

		// find retrieves the first entry that passes the condition.
		// documentation for _.find() http://underscorejs.org/#find
		return _.find(service.entries, function(entry){return entry.companyId == id});
	}

	// update an entry
	service.save = function(entry) {
		// find element we want to update
		var toUpdate = service.getById(entry.companyId);

		// if exists we update
		if(toUpdate) {
			// update in the cloud
			$http.put('companies/update/'+entry.companyId, entry).
			success(function(data){
				// we'll copy all the properties from "entry" to the object
				// we want to update
				// documentation for _.extend: http://underscorejs.org/#extend
				_.extend(toUpdate, entry);

			}).
			error(function(data, status){
				alert('error!');
			});            
		}
		// otherwise we create it
		else {
			// push new entry to the cloud
			$http.post('companies/add/', entry).
			success(function(data){
				entry.companyId = data.companyId;
				service.entries.push(entry);
			}).
			error(function(data, status){
				alert('error!');
			});      
		}    
	}

	// remove an entry
	service.remove = function(entry,index) {
		// documentation for _.reject(): http://underscorejs.org/#reject
		// delete in the cloud, if successfull update client side
		$http.delete('companies/delete/'+ entry.companyId).
		success(function(data){
			service.entries = _.reject(service.entries, function(element){
				return element.id == entry.companyId;
			});
			service.entries.splice(index, 1)

		}).
		error(function(data, status){
			alert('error!');
		})	
	}

	return service;
});

// listing of all expenses
app.controller('CompaniesViewControl', ['$scope', 'Companies', function($scope, Companies) {
	$scope.companies = Companies.entries;

	$scope.remove = function(company) {
		Companies.remove(company);	
	};

	// we need to watch the list of expenses more closely to have it always
	// updated
	$scope.$watch(function () { return Companies.entries; }, function (entries) {
		$scope.companies = entries;
	});

}]);

// create or edit an expense
app.controller('CompanyViewControl', ['$scope', '$routeParams', '$location', 'Companies', function($scope, $routeParams, $location, Companies) {

	// the Company will either be a new one or existing one if we are
	// editing
	if(!$routeParams.id) {
		$scope.company = {date: new Date()}
	}
	else {
		// clone makes a copy of an object, so we don't modify the real object
		// before clicking Save
		$scope.company = _.clone(Companies.getById($routeParams.id));
	}

	// push the company to the array of companies. Duplicate entries will
	// thow error unless adding "track by $index" to the ng-repeat directive
	$scope.save = function() {
		Companies.save($scope.company);          
		$location.path('/');
	};

	$scope.$watch(function () { return Companies.entries; }, function (entries) {
		$scope.companies = entries;
	});


}]);

// we create a custom directive so we can use the tag <expense>
// doc: https://docs.angularjs.org/guide/directive, tutorial:
// http://www.ng-newsletter.com/posts/directives.html
app.directive('companyview', function(){
	return {
		restrict: 'E',  // it means it's for elements (custom html tags)
		templateUrl: './views/company.html',
		// template: '<div>{{expense.description}}</div>'
	};
});
(function(angular){
"use strict";

	angular.module('users-board', ['users'])
	.controller('Profile', ['$LoggedUser', '$log', function($LoggedUser, $log) {
		var self = this;
		$LoggedUser.get()
		.then(function(userData){
			self.user = userData;
		})
		.catch(function(err){
			$log.error(err);
			$log.info('No user to authenticate. Sign in first.');
		});	
	}])
	.controller('ProfileEdit', ['$window', 'FileUploader', '$LoggedUser', '$log', function($window, FileUploader, $LoggedUser, $log){
		var self = this;
		var uploader = this.uploader = new FileUploader();	
		$LoggedUser.get()
		.then(function(userData){
			self.user = userData;
			uploader.url = self.user.avatarUrl;
		})
		.catch(function(err){
			$log.error(err);
			if(err.status && err.status===404){
				$log.info('No user to authenticate. Sign in first.');
			}
		});			
		
	    this.uploader.onCompleteItem = function(/*fileItem, response, status, headers*/) {
			//$state.reload();
	    	$LoggedUser.get()
			.then(function(userData){
			 	$window.location.reload();
			});				
	    };
	    this.uploader.onAfterAddingFile = function(/*fileItem*/) {
	    	self.uploader.uploadAll();
	    };
	}]);

})(angular);

(function(angular){
"use strict";

//angular'ized extenral dependencies
angular.module('$moment', [])
.factory('$moment', ['$window', function($window) {
  return $window.moment;
}]);

angular.module('$ckeditor', [])
.factory('$ckeditor', ['$window', function($window) {
  return $window.CKEDITOR;
}]);


angular.module('discussion-boards', ['$moment', '$ckeditor', 'ngSanitize', 'ngAnimate', 'ngResource', 'ui.router', 'ui.bootstrap', 'angular-loading-bar', 'angularFileUpload','angular-timeline','angular-scroll-animate'])
.config(['$stateProvider', '$urlRouterProvider', 'cfpLoadingBarProvider', function($stateProvider, $urlRouterProvider, cfpLoadingBarProvider) {

		$urlRouterProvider.otherwise("/");
		
		$stateProvider	
		.state('list', {
			  url: "/",
		      views: {
		      	"@": {
		              templateUrl: 'views/master.html',
		              controller: ['$Boards', '$log', 'FilterList', function($Boards, $log, FilterList){
		              
		              	this.list = [];
		              	this.filterList = FilterList;
		              	var self = this;
		              	
						$Boards.list()
						.then(function(data){
							self.list = data;
						})
		              	.catch(function(err){
		              		$log.error(err);
		              		throw err;
		              	});
		              	
						this.saveVote = function(board, vote){
							$Boards.saveVote(board, vote)
							.then(function(data){
								$log.info("voted: " + vote);
								self.list = self.list.map(function(b){
									if(b.id === data.id)
										return data;
									else
										return b;
								});
							});
						};
						
		              }],
		              controllerAs: 'masterVm'
		      	},
		      	"toolbar@": {
		              templateUrl: 'views/toolbar.html',
		              controller: ['FilterList', function(FilterList){
		              	this.filterList = FilterList;
		              }],
		              controllerAs: 'toolbarVm'
		      	}
		      }
		    })
		.state('list.entity', {
			url: "{boardId}",
			params: {
				board: undefined,
				timeline: false
			},
			resolve: {
				board: ['$state', '$stateParams', '$Boards', '$log', function($state, $stateParams, $Boards, $log){
					var boardId;
					if($stateParams.boardId !==undefined &&  $stateParams.boardId!==''){
						boardId = $stateParams.boardId;
					}
					if(boardId){
						if($stateParams.board)
							return $stateParams.board;
						else
							return $Boards.get(boardId)
							.catch(function(err){
								$log('Could not resolveboard entity with id['+$stateParams.boardId+']');
								$state.go('list');
							});
					} else {
						return;
					}
				}]
			},
			views: {
				"@": {
					templateUrl: "views/detail.html",				
					controller: ['$state', '$stateParams', '$log', '$Boards', '$DBoardVisits', 'board', function($state, $stateParams, $log, $Boards, $DBoardVisits, board){
						this.board = board;
						var self = this;
						
						try{
							$DBoardVisits.visit(this.board.id)
							.then(function(res){
								if(res!==false)
									self.board.visits++;
							});
						} catch(err){$log.error(err);}
						
						if($stateParams.timeline){
							$state.go('list.entity.discussion-timeline', {boardId: self.board.id, board:self.board}); 
						} else {
							$state.go('list.entity.discussion', {boardId: self.board.id, board:self.board});  	
						}
						
						this.saveVote = function(vote){
							$Boards.saveVote(self.board, vote)
							.then(function(data){
								$log.info("voted: " + vote);
								self.board = data; 
							});
						};
						
						this.getVote = function(){
							$Boards.getVote(self.board)
							.then(function(vote){
								self.currentUserVote = vote;
							});
						};
						
						this.openBoardForEdit = function(){
							self.descriptionEdit = self.board.description;
						};
						
						this.postEdit = function(){
							self.board.description = self.descriptionEdit;
							$Boards.update(self.board)
							.then(function(board){
								self.board = board;
								delete self.descriptionEdit; 
							});
						};
						
						this.cancelEdit = function(){
							delete self.descriptionEdit;
						};

					}],
					controllerAs: 'detailsVm'				
				}
			}
		})
		.state('list.entity.discussion-timeline', {
			resolve: {
				comments: ['BoardCommentsTimeline', 'board', function(BoardCommentsTimeline, board){
					return BoardCommentsTimeline
							.get({boardId: board.id}).$promise.
							then(function(comments){
								return comments;
							});
				}]
			},
			views: {
				"@list.entity": {
					templateUrl: "views/discussion-timeline.html",				
					controller: ['$state', '$log', '$Boards', '$Comment','$UserImg', 'board', 'comments', function($state, $log, $Boards, $Comment, $UserImg, board, comments){
						
						this.comment = {};
						this.board = board;
						this.comments = comments;
						var self = this;
					  	
					  	this.openCommentForEdit = function(comment){
					  		self.comment = comment;
					  		self.commentEdit = true;
					  	};
					  	
					  	this.cancelCommentEdit = function(){
					  		self.comment = {};
					  		self.commentEdit = false;
					  	};
					  	
						this.postComment = function(){
							self.comment.id = this.board.id;
							var operation = self.comment.id!==undefined?'update':'save';
							$Comment[operation](self.comment).$promise
							.then(function(commentData){
								//TODO: mixin into the resource the id from Location header upon response
								$log.info('Comment with id['+commentData.id+'] saved');
								$Boards.get(board.id)
								.then(function(board){
									$state.go('list.entity', {board: board, timeline: true});
									//$state.go('list.entity.discussion-timeline', {board: board});
								});
							})
							.catch(function(err){
								$log.error(err);
								throw err;
							})
							.finally(function(){
								self.cancelCommentEdit();
							});
						};
						
						this.pix = {};
						
						for(var i=0; i<this.comments.length; i++){
							if(this.pix[this.comments[i].user] === undefined)
								this.pix[this.comments[i].user]= '';
						}
						
						for(var user in this.pix){
							getUserPicture(user)
							.then(function(pic){
								if(pic){
									self.pix[user] = "/services/js/idm/svc/user.js/$pics/"+user;
								}
							});
						}
						
						function getUserPicture(username){
							return $UserImg.get(username)
							.then(function(data){
								return data;
							});
						};

					}],
					controllerAs: 'vm'				
				}
			}
		})		
		.state('list.entity.discussion', {
			views: {
				"@list.entity": {
					templateUrl: "views/discussion.html",				
					controller: ['$state', '$log', '$Boards', '$Comment', 'board', function($state, $log, $Boards, $Comment, board){
						
						this.comment = {};
						this.board = board;
						var self = this;
					  	
					  	this.openCommentForEdit = function(comment){
					  		self.comment = comment;
					  		self.commentEdit = true;
					  		if(self.replyEdit)
					  			self.replyCancel();
					  	};
					  	
					  	this.cancelCommentEdit = function(){
					  		self.comment = {};
					  		self.commentEdit = false;
					  	};
					  	
						this.postComment = function(){
							self.comment.boardId = this.board.id;
							var operation = self.comment.id!==undefined?'update':'save';
							$Comment[operation](self.comment).$promise
							.then(function(commentData){
								//TODO: mixin into the resource the id from Location header upon response
								$log.info('Comment with id['+commentData.id+'] saved');
								$Boards.get(board.id)
								.then(function(board){
									$state.go('list.entity', {board: board}, {reload:true});
								});
							})
							.catch(function(err){
								$log.error(err);
								throw err;
							})
							.finally(function(){
								self.cancelCommentEdit();
							});
						};
						
						this.replyOpen = function(comment, reply){
							self.comment = comment;
							self.replyEdit = true;
							self.reply = reply || {
								replyToCommentId: comment.id,
								boardId: self.board.id
							};
						};

						this.replyCancel = function(){
							delete self.reply;
							self.replyEdit = false;
							if(!self.commentEdit && self.comment.id!==undefined)
								self.cancelCommentEdit();
						};

						this.replyPost = function(){
							var upsertOperation = self.reply.id===undefined?'save':'update';
							$Comment[upsertOperation ](self.reply).$promise
							.then(function(){
								$log.info('reply saved');
								$Boards.get(board.id)
								.then(function(board){
									$state.go('list.entity', {board: board}, {reload:true});
								});
							})
							.catch(function(err){
								throw err;
							})
							.finally(function(){
								self.replyCancel();
							});
						};

					}],
					controllerAs: 'vm'				
				}
			}
		})		
		.state('list.new', {    
			views: {
				"@": {
					templateUrl: "views/board.form.html",
					controller: ['$state', '$log', 'Board',  function($state, $log, Board){
							this.board = {};
					  		this.submit = function(){
					  			Board.save(this.board).$promise
					  			.then(function(data){
					  				$log.info('board with id['+data.id+'] saved');
		              				$state.go('list');
					  			})
					  			.catch(function(err){
					  				$log.error('board could not be saved');
					  				throw err;
					  			});
					  		};
						}],
					controllerAs: 'detailsVm'								
				}
			}
		})
		.state('list.settings', {  
			resolve: {
				user: ['$LoggedUser', function($LoggedUser){
					return $LoggedUser.get()
						.then(function(user){
							return user;
						});	
				}]
			},
			views: {
				"@": {
					templateUrl: "views/settings.html",	
					controller: ['$state', 'FileUploader', 'user', function($state, FileUploader, user){
						
						this.user = user;
						var self  = this;
						
						var uploader = this.uploader = new FileUploader({
							url: this.user.avatarUrl
						});
					    this.uploader.onBeforeUploadItem = function(/*item*/) {
							//item.url = zipUploadPath + "?path=" + this.folder.path;
					    };
					    this.uploader.onCompleteItem = function(/*fileItem, response, status, headers*/) {
							$state.reload();
					    };
					    this.uploader.onAfterAddingFile = function(/*fileItem*/) {
					    	self.uploader.uploadAll();
					    };
					}],
					controllerAs: 'settingsVm'
				}
			}
		});
		  
		cfpLoadingBarProvider.includeSpinner = false;
		  
	}]);
})(angular);

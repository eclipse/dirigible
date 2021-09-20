(function (angular) {
	"use strict";

	//angular'ized external dependencies
	angular.module('$moment', [])
		.factory('$moment', ['$window', function ($window) {
			let locale = $window.navigator.userLanguage || $window.navigator.language;
			$window.moment && $window.moment.locale(locale);
			return $window.moment;
		}]);

	angular.module('$ckeditor', [])
		.factory('$ckeditor', ['$window', function ($window) {
			return $window.CKEDITOR;
		}]);

	angular.module('discussion-boards', ['$moment', '$ckeditor', 'ngSanitize', 'ngAnimate', 'ngResource', 'ui.router', 'ui.bootstrap', 'angular-loading-bar', 'angularFileUpload', 'angular-timeline', 'angular-scroll-animate', 'ngTagsInput', 'i18n'])
		.constant('CONFIG', {
			"LOGIN_URL": "login/login.html",
			"features": {
				"list": {
					"pageLimit": 25
				},
				"votes": {
					"enabled": true
				},
				"tags": {
					"enabled": false
				}
			}
		})
		.config(['$stateProvider', '$urlRouterProvider', 'cfpLoadingBarProvider', 'CONFIG', function ($stateProvider, $urlRouterProvider, cfpLoadingBarProvider, CONFIG) {

			$urlRouterProvider.otherwise("/");

			$stateProvider
				.state('list', {
					url: "/",
					resolve: {
						loggedUser: ['$LoggedUser', '$log', function ($LoggedUser, $log) {
							return $LoggedUser.get()
								.then(function (user) {
									return user;
								})
								.catch(function (err) {
									$log.error(err);
									if (err.status && [404, 401, 403].indexOf(err.status) > -1)
										$log.info('No user to authenticate. Sign in first.');
									return;
								});
						}],
						i18n: ['i18n', function (i18n) {
							return i18n;
						}]
					},
					views: {
						"@": {
							templateUrl: 'views/boards.list.html',
							controller: ['$Boards', '$log', 'FilterList', 'i18n', 'CONFIG', function ($Boards, $log, FilterList, i18n, CONFIG) {
								this.CONFIG = CONFIG;
								this.i18n = i18n;
								this.list = [];
								this.filterList = FilterList;
								this.limit = CONFIG.features.list.pageLimit;
								this.offset = 0;
								this.hasMore = false;
								let next = this.next = function (_offset, _limit) {
									if (_offset !== undefined && _offset === 0)
										this.list = [];
									$Boards.list({
										$limit: _limit || this.limit,
										$offset: _offset || this.offset
									})
										.then(function (data) {
											this.list = this.list.concat(data.entities);
											this.hasMore = data.count > this.list.length;
											this.offset += this.limit;
										}.bind(this))
										.catch(function (err) {
											$log.error(err);
											throw err;
										});
								}.bind(this);

								next();

							}],
							controllerAs: 'boardsVm'
						},
						"toolbar@": {
							templateUrl: 'views/toolbar.html',
							controller: ['FilterList', 'loggedUser', 'i18n', function (FilterList, loggedUser, i18n) {
								this.i18n = i18n;
								this.filterList = FilterList;
								this.loggedUser = loggedUser;
							}],
							controllerAs: 'toolbarVm'
						}
					}
				})
				.state('logout', {
					views: {
						"@": {
							template: '',
							controller: ['$state', '$LoggedUserProfile', function ($state, $LoggedUserProfile) {
								$LoggedUserProfile.logout()
									.finally(function () {
										$state.go('list');
									});
							}]
						}
					}
				})
				.state('list.login', {
					url: "login",
					views: {
						"@": {
							template: '',
							controller: ['$window', function ($window) {
								$window.location.href = CONFIG.LOGIN_URL;
							}],
							controllerAs: 'loginVm'
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
						board: ['$state', '$stateParams', '$Boards', '$log', function ($state, $stateParams, $Boards, $log) {
							//The settings deep link url resolve true from the pattern for dboard detail so we need to explicitly check if it is setting that have been requested or a board details
							if ($stateParams.boardId === 'settings') {
								return;
							}
							let boardId;
							if ($stateParams.boardId !== undefined && $stateParams.boardId !== '') {
								boardId = $stateParams.boardId;
							}
							if (boardId) {
								if ($stateParams.board) {
									return $stateParams.board;
								} else
									return $Boards.get(boardId)
										.catch(function (err) {
											$log.error('Could not resolve board entity with id[' + $stateParams.boardId + ']');
											$state.go('list');
										});
							} else {
								return;
							}
						}]
					},
					views: {
						"@": {
							templateUrl: "views/board.html",
							controller: ['$state', '$stateParams', '$log', '$Boards', '$Tags', 'board', 'loggedUser', '$rootScope', 'CONFIG', 'i18n', function ($state, $stateParams, $log, $Boards, $Tags, board, loggedUser, $rootScope, CONFIG, i18n) {
								//The settings deep link url resolve true from the pattern for dboard detail so we need to explicitly check if it is setting that have been requested or a board details
								if ($stateParams.boardId === 'settings') {
									$state.go('list.settings');
									return;
								}
								this.CONFIG = CONFIG;
								this.i18n = i18n,
									this.board = board;
								this.commentsCount = board.commentsCount;
								this.loggedUser = loggedUser;
								let self = this;

								$rootScope.$on('dboards.comments.save',
									function (evt) {
										self.commentsCount++;
										evt.preventDefault();
									});
								$rootScope.$on('dboards.comments.remove',
									function (evt, comment) {
										self.commentsCount--;
										if (comment.replies)
											self.commentsCount = self.commentsCount - comment.replies.length;
										evt.preventDefault();
									});
								$Boards.visit(this.board);

								if ($stateParams.timeline) {
									$state.go('list.entity.discussion.timeline', { boardId: self.board.id, board: self.board, timeline: true });
								} else {
									$state.go('list.entity.discussion.thread', { boardId: self.board.id, board: self.board, timeline: false });
								}

								this.hasPrivilege = function (privilege) {
									if (this.loggedUser !== undefined) {
										return $Boards.hasPrivilege(this.loggedUser.username, privilege, this.board);
									}
									return false;
								};

								this.hasMode = function () {
									let args = [].slice.call(arguments);
									if (args.indexOf('readonly') > -1 && self.loggedUser !== undefined && self.board.locked)
										return true;
									if (args.indexOf('edit') > -1 && self.loggedUser !== undefined && !self.board.locked && self.descriptionEdit)
										return true;
									return false;
								};

								this.saveVote = function (vote) {
									$Boards.saveVote(self.board, vote)
										.then(function (data) {
											$log.info("voted: " + vote);
											self.board = data;
										});
								};

								this.openBoardForEdit = function () {
									self.descriptionEdit = self.board.description;
								};

								this.postEdit = function () {
									self.board.description = self.descriptionEdit;
									$Boards.update(self.board)
										.then(function (board) {
											self.board = board;
											delete self.descriptionEdit;
										});
								};

								this.cancelEdit = function () {
									delete self.descriptionEdit;
								};

								this.toggleLock = function () {
									let op = self.board.locked ? 'unlock' : 'lock';
									$Boards[op].apply(self, [self.board])
										.then(function () {
											$stateParams.board = self.board;
											$state.go($state.$current, $stateParams);
										});
								};

								this.remove = function () {
									$Boards.remove(self.board)
										.then(function () {
											$state.go('list');
										});
								};

								this.tagAdded = function ($tag) {
									let tags = self.board.tags.map(function (tag) {
										return tag.defaultLabel;
									});
									$Boards.setTags(self.board, tags);
								};

								this.tagRemoved = function ($tag) {
									let tags = self.board.tags.map(function (tag) {
										return tag.defaultLabel;
									});
									$Boards.setTags(self.board, tags);
								};

								this.loadTags = function (query) {
									return $Tags.query({ "defaultLabel": query, "$filter": "defaultLabel" }).$promise
										.then(function (tags) {
											return tags;
										});
								};

							}],
							controllerAs: 'boardVm'
						}
					}
				})
				.state('list.entity.discussion', {
					abstract: true
				})
				.state('list.entity.discussion.thread', {
					url: "/thread",
					resolve: {
						comments: ['$Comments', 'board', function ($Comments, board) {
							return $Comments
								.list(board.id, 'thread')
								.then(function (comments) {
									return comments;
								});
						}]
					},
					views: {
						"@list.entity": {
							templateUrl: "views/discussion.thread.html",
							controller: ['$log', '$Comments', 'board', 'comments', 'loggedUser', 'i18n', function ($log, $Comments, board, comments, loggedUser, i18n) {
								this.i18n = i18n,
									this.comment = {};
								this.comments = comments;
								this.board = board;
								this.loggedUser = loggedUser;
								let self = this;

								this.hasPrivilege = function (comment, privilege) {
									if (this.loggedUser !== undefined) {
										return $Comments.hasPrivilege(this.loggedUser.username, privilege, comment, this.board);
									}
									return false;
								};

								this.hasMode = function () {
									let args = [].slice.call(arguments);
									if (args.indexOf('readonly') > -1 && self.loggedUser !== undefined && self.board.locked)
										return true;
									if (args.indexOf('edit') > -1 && self.loggedUser !== undefined && !self.board.locked && (self.commentEdit || self.replyEdit))
										return true;
									return false;
								};

								this.openCommentForEdit = function (comment) {
									self.comment = comment;
									self.commentEdit = true;
									if (self.replyEdit)
										self.replyCancel();
								};

								this.cancelCommentEdit = function () {
									self.comment = {};
									self.commentEdit = false;
								};

								this.postComment = function () {
									let operation = self.comment.id !== undefined ? 'update' : 'save';
									if (operation === 'save') {
										self.comment.boardId = this.board.id;
										self.comments.push(self.comment);
									}
									let comment = self.comment;
									self.cancelCommentEdit(); self.commentEdit = true;
									$Comments[operation](comment)
										.then(function (commentData) {
											$log.info('Comment[' + commentData.id + '] ' + operation + 'd');
											$Comments.list(self.board.id, 'thread')
												.then(function (comments) {
													self.comments = comments;
												})
												.finally(function () {
													self.commentEdit = false;
												});
										})
										.catch(function (err) {
											$log.error(err);
											self.comments.filter(function (com) {
												return com.id === comment;
											});
											self.commentEdit = false;
											throw err;
										});
								};

								this.replyOpen = function (comment, reply) {
									self.comment = comment;
									self.replyEdit = true;
									self.reply = reply || {
										replyToCommentId: comment.id,
										boardId: self.board.id
									};
								};

								this.replyCancel = function () {
									delete self.reply;
									self.replyEdit = false;
									if (!self.commentEdit && self.comment.id !== undefined)
										self.cancelCommentEdit();
								};

								this.replyPost = function () {
									let operation = self.reply.id === undefined ? 'save' : 'update';
									if (operation === 'save') {
										self.reply.replyToCommentId = self.comment.id;
										self.comment.replies.push(self.reply);
									}
									let reply = self.reply;
									this.replyCancel(); self.replyEdit = true; self.commentEdit = true;
									$Comments[operation](reply)
										.then(function (replyData) {
											$log.info('Reply[' + replyData.id + '] to comment[' + self.comment.id + '] ' + operation + 'd');
											$Comments.list(self.board.id, 'thread')
												.then(function (comments) {
													self.comments = comments;
												})
												.finally(function () {
													self.replyEdit = false;
													self.commentEdit = false;
												});
										})
										.catch(function (err) {
											self.replyEdit = false;
											self.commentEdit = false;
											throw err;
										});
								};

								this.remove = function (comment) {
									this.comments = this.comments.filter(function (com, idx) {
										if (com.id === comment.id) {
											return false;
										} else {
											self.comments[idx].replies = self.comments[idx].replies.filter(function (_com) {
												if (_com.id === comment.id) {
													return false;
												}
												return true;
											});
											return true;
										}
									});
									$Comments.remove(comment)
										.catch(function (err) {
											throw err;
										});
								};

							}],
							controllerAs: 'vm'
						}
					}
				})
				.state('list.entity.discussion.timeline', {
					url: "/timeline",
					resolve: {
						comments: ['$Comments', 'board', function ($Comments, board) {
							return $Comments
								.list(board.id, 'timeline')
								.then(function (comments) {
									return comments;
								});
						}]
					},
					views: {
						"@list.entity": {
							templateUrl: "views/discussion.timeline.html",
							controller: ['$Comments', '$log', 'board', 'comments', 'loggedUser', 'CONFIG', 'i18n', function ($Comments, $log, board, comments, loggedUser, CONFIG, i18n) {
								this.i18n = i18n;
								this.CONFIG = CONFIG;
								this.comment = {};
								this.board = board;
								this.comments = comments;
								this.loggedUser = loggedUser;
								let self = this;

								this.hasPrivilege = function (comment, privilege) {
									if (this.loggedUser !== undefined) {
										return $Comments.hasPrivilege(this.loggedUser.username, privilege, comment, this.board);
									}
									return false;
								};

								this.hasMode = function () {
									let args = [].slice.call(arguments);
									if (args.indexOf('readonly') > -1 && self.loggedUser !== undefined && self.board.locked)
										return true;
									if (args.indexOf('edit') > -1 && self.loggedUser !== undefined && !self.board.locked && self.commentEdit)
										return true;
									return false;
								};

								this.openCommentForEdit = function (comment) {
									self.comment = comment;
									self.commentEdit = true;
								};

								this.cancelCommentEdit = function () {
									self.comment = {};
									self.commentEdit = false;
								};

								this.postComment = function () {
									self.comment.boardId = this.board.id;
									let operation = self.comment.id !== undefined ? 'update' : 'save';
									if (operation === 'save')
										self.comments.push(self.comment);
									$Comments[operation](self.comment)
										.then(function (commentData) {
											$log.info('Comment[' + commentData.id + '] ' + operation + 'd');
											$Comments.list(self.board.id, 'thread')
												.then(function (comments) {
													self.comments = comments;
												});
										})
										.catch(function (err) {
											$log.error(err);
											throw err;
										})
										.finally(function () {
											self.cancelCommentEdit();
										});
								};

								this.remove = function (comment) {
									this.comments = this.comments.filter(function (com) {
										if (com.id === comment.id)
											return false;
										return true;
									});
									$Comments.remove(comment)
										.catch(function (err) {
											throw err;
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
							controller: ['$state', '$log', 'SecureBoard', 'loggedUser', 'i18n', function ($state, $log, SecureBoard, loggedUser, i18n) {
								if (!loggedUser) {
									$state.go('list.login');
									return;
								}
								this.i18n = i18n;
								this.board = {};
								this.submit = function () {
									SecureBoard.save(this.board).$promise
										.then(function (data) {
											$log.info('board with id[' + data.id + '] saved');
											$state.go('list');
										})
										.catch(function (err) {
											$log.error('board could not be saved');
											throw err;
										});
								};
							}],
							controllerAs: 'boardVm'
						}
					}
				})
				.state('list.settings', {
					url: '/settings',
					views: {
						"@": {
							templateUrl: "views/settings.html",
							controller: ['$state', 'FileUploader', 'loggedUser', 'i18n', function ($state, FileUploader, loggedUser, i18n) {
								this.i18n = i18n;
								this.user = loggedUser;
								this.rootPath = '/services/v4/js/ide-discussions/svc/user/avatar.js';
								let self = this;

								let uploader = this.uploader = new FileUploader({
									url: this.user && this.rootPath
								});
								this.uploader.onBeforeUploadItem = function (/*item*/) {

								};
								this.uploader.onCompleteItem = function (/*fileItem, response, status, headers*/) {
									$state.reload();
								};
								this.uploader.onAfterAddingFile = function (/*fileItem*/) {
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
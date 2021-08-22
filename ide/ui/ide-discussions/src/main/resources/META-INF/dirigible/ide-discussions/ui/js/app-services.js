(function(angular){
"use strict";
	angular.module('discussion-boards')
	.service('$TimeFormat', ['$moment', function($moment){
		function asElapsedTimeString(time){
			if(time)
				return $moment(new Date(time).toISOString()).fromNow();
		}
		function asFormattedTimeString(time){
			return $moment(time).format('LLL');
		}		
		return {
			asElapsedTimeString: asElapsedTimeString,
			asFormattedTimeString: asFormattedTimeString
		};
	}])
	.service('$Comments', ['BoardComments', '$SecureComment', '$TimeFormat', '$moment', '$rootScope', function(BoardComments, $SecureComment, $TimeFormat, $moment, $rootScope){
	
		var formatComment = function(comment){
			comment.timeSincePublish = $TimeFormat.asElapsedTimeString(comment.publishTime);
			comment.publishTimeLocal = $TimeFormat.asFormattedTimeString(comment.publishTime); 	      			
      		if($moment(comment.lastModifiedTime).isAfter($moment(comment.publishTime))){
      			comment.lastModifiedTimeLocal = $TimeFormat.asFormattedTimeString(comment.lastModifiedTime);
      			comment.timeSinceLastModified = $TimeFormat.asElapsedTimeString(comment.lastModifiedTime);
  			}
            return comment;
		};
	
		var list = function(boardId, mode){
			if(mode && ['timeline', 'thread'].indexOf(mode)<0)
				throw Error('Unknown list mode for requesting board comments: ' + mode);
			var params = {};
			params['boardId']=boardId;
			if(mode === 'thread'){
				params['$expand']='replies';
				params['thread']=true;
			}
			return BoardComments
					.query(params).$promise.
					then(function(commentsData){
						var comments = commentsData.filter(function(comment){
							return comment.replyToCommentId===undefined;
						})
						.map(function(comment){
							return formatComment(comment);
						});
						return comments;
					});
		};
		
		var save = function(comment){
			$rootScope.$broadcast('dboards.comments.save', comment);
			return $SecureComment.save(comment).$promise;		
		};
		
		var update = function(comment){
			return $SecureComment.update(comment).$promise;		
		};
		
		var remove = function(comment){
			$rootScope.$broadcast('dboards.comments.remove', comment);
			return $SecureComment['delete']({"commentId": comment.id}).$promise;		
		};

	  	var hasPrivilege = function(username, privilege, comment, board){
  			if(privilege === 'edit' && comment.user === username)
  				return true;
  			if(privilege === 'delete' && [board.user, comment.user].indexOf(username) > -1)
  				return true;
			if(privilege === 'comment' && username!==undefined)
  				return true;
			if(privilege === 'reply' && username!==undefined)
  				return true;
	  		return false;
	  	};
	
		return {
			list: list,
			formatComment: formatComment,
			save: save,
			update: update,
			remove: remove,
			hasPrivilege: hasPrivilege
		};
	}])	
	.service('$Boards', ['Board', 'SecureBoard', 'BoardVisits', 'BoardVote', 'SecureBoardVote', 'BoardTags', 'SecureBoardTags', '$Comments', '$moment', '$log', function(Board, SecureBoard, BoardVisits, BoardVote, SecureBoardVote, BoardTags, SecureBoardTags, $Comments, $moment, $log) {
		
		function asElapsedTimeString(time){
			if(time)
				return $moment(new Date(time).toISOString()).fromNow();
		}
		
		function formatNumberShort(value){
			if(value === undefined || Number.isNaN(value))
				return value;
			var val = parseInt(value);
			if(val > 1000000){
				val = Math.round((val/1000000)*100)/100;
				return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")+"M+";
			}
			if(val>1000){
				val = Math.round((val/1000)*100)/100;
				return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")+"K+";
			}
			if(val > 200){
				return Math.floor(100/100)*100 + '+';
			}
			return val;
		}
		
		function formatEntity(board){
			board.timeSincePublish = asElapsedTimeString(board.publishTime);
			board.publishTimeLocal = $moment(board.publishTime).format('LLL');
			board.lastModifiedTimeLocal = $moment(board.lastModifiedTime).format('LLL');
  			if($moment(board.lastModifiedTime).isAfter($moment(board.publishTime))){
  				board.timeSinceLastModified = asElapsedTimeString(board.lastModifiedTime);			
  			}
			if(board.latestDiscussionUpdateTime){
				board.latestDiscussionUpdateTimeLocal = $moment(board.latestDiscussionUpdateTime).format('LLL');  	
  				board.timeSinceLatestDiscussionUpdateTime = asElapsedTimeString(board.latestDiscussionUpdateTime); 
  			}  			
  			if(board.comments){
          		board.comments = board.comments.map(function(comment){
	      			var comment = $Comments.formatComment(comment);
	      			if(comment.replies){
	      				comment.replies = comment.replies.map(function(reply){
	      					return $Comments.formatComment(reply);
	          			});
	            	}
	            	return comment;
				});
  			}
  			
  			board.visitsShort = formatNumberShort(board.visits);
  			board.participantsCountShort = formatNumberShort(board.participantsCount);
			board.totalVotesShort = formatNumberShort(board.totalVotes);
			board.upvotesShort = formatNumberShort(board.upvotes);
			board.downvotesShort = formatNumberShort(board.downvotes);			
			board.ratingShort = formatNumberShort(board.rating);
  			return board;
		}

		var visits = [];
		var visit = function(board){
			if(visits.indexOf(board.id)<0){
				visits.push(board.id);
				board.visitsShort = formatNumberShort(++board.visits);
				BoardVisits.update({"boardId": board.id}, {}).$promise
				.catch(function(err){
					board.visitsShort = formatNumberShort(--board.visits);
					$log.error(err.message)
				});
			}
		};
		var list = function(params){
			params = params || {};
			params['$expand'] = 'votes,tags';
			return Board.query(params).$promise
          	.then(function(data){
          		var _entities = (data.entities || data).map(function(boardData){
          			return formatEntity(boardData);
          		});
          		var res = _entities;
          		if(params.$limit!==undefined){
          			res = {
	          			entities: _entities,
	          			count: data.$count
	          		};
          		}
          		return res;
          	});          	
		};
		var get = function(boardId){
			return Board.get({"boardId": boardId, "$expand":"votes,tags,comments"}).$promise
			.then(function(board){
	      		return formatEntity(board);
			});
		};
		var update = function(board){
			return SecureBoard.update(board).$promise
			.then(function(board){
	      		return formatEntity(board);
			});
		};	
		var remove = function(board){
			if(visits.indexOf(board.id)>-1)
				visits.splice(visits.indexOf(board.id), 1);
			return SecureBoard.remove({boardId: board.id, cascaded:true}).$promise;
		};
		var saveVote = function(board, v){
			return SecureBoardVote.save({"boardId": board.id}, {"vote":v}).$promise
			.then(function(){
	      		return get(board.id);
			});
		};
		var getVote = function(board){
			return BoardVote.get({"boardId":board.id}).$promise
			.then(function(vote){
	      		return vote;
			});
		};
		var getTags = function(board){
			return BoardTags.get({"boardId":board.id}).$promise
			.then(function(tags){
	      		return tags;
			});
		};
		var setTags = function(board, tags){
			return SecureBoardTags.save({"boardId": board.id}, tags).$promise;
		};
		var untag = function(board, tags){
			return SecureBoardTags.remove({"boardId": board.id}, tags).$promise;
		};
		var lock = function(board){
			board.locked = true;
			return SecureBoard.update({"boardId": board.id}, board).$promise;
		};		
		var unlock = function(board){
			board.locked = false;
			return SecureBoard.update({"boardId": board.id}, board).$promise;
		};
		
	  	var hasPrivilege = function(username, privilege, board){
			if(['lock', 'tag', 'delete', 'edit'].indexOf(privilege)>-1 && board.user === username)
  				return true;
			if(privilege === 'vote' && board.user !== username)
  				return true;
	  		return false;
	  	};		

	 	return {
	 		list: list,
	 		get :get,
	 		update: update,
	 		remove: remove,
	 		getVote: getVote,
	 		saveVote: saveVote,
	 		getTags: getTags,
	 		setTags: setTags,
	 		untag: untag,
	 		lock: lock,
	 		unlock:unlock,
	 		visit: visit,
	 		hasPrivilege: hasPrivilege
	 	};
	}])	
	.service('FilterList', [function() {
		var _filterText;
	  	return {
	  		filterText: _filterText
	  	};
	}]);
})(angular);	

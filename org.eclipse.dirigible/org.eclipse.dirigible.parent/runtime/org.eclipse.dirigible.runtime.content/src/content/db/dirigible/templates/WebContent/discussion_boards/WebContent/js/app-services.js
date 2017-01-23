(function(angular){
"use strict";
	angular.module('discussion-boards')
	.service('$DBoardVisits', ['BoardVisits', '$q', function(BoardVisits, $q) {
		var visited = [];
		var put = function(boardId){
			if(visited.indexOf(boardId)<0){
				visited.push(boardId);
				return BoardVisits.update({"boardId": boardId}, {}).$promise;
			} else {
				return $q.when(false);
			}
		};
	  	return {
	  		visit: put
	  	};
	}])
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
	.service('$Comments', ['BoardComments', '$TimeFormat', '$moment', function(BoardComments, $TimeFormat, $moment){
	
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
			if(mode && mode!=='timeline')
				throw Error('Unknown list mode for requesting board comments: ' + mode);
			return BoardComments
					.get({boardId: boardId, listMode: mode}).$promise.
					then(function(commentsData){
						var comments = commentsData.map(function(comment){
							return formatComment(comment);
						});
						return comments;
					});
		};
	
		return {
			list: list,
			formatComment: formatComment
		};
	}])	
	.service('$Boards', ['Board', 'BoardVote', 'BoardTags', '$Comments', '$UserImg', '$moment', function(Board, BoardVote, BoardTags, $Comments, $UserImg, $moment) {
		
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
		
		var list = function(){
			return Board.query({expanded:true}).$promise
          	.then(function(data){
          		return data.map(function(boardData){
          			var board = formatEntity(boardData);
          			board.userDetails = {
						avatar: "/services/js/usr/svc/user.js/$pics/"+board.user 
					};
					$UserImg.get(board.user)
					.then(function(image){
						if(!image){
							board.userDetails.avatar = undefined;
						}
					});
          			return board;
          		});
          	});          	
		};
		var get = function(boardId){
			return Board.get({"boardId": boardId, "expanded":true}).$promise
			.then(function(board){
	      		board = formatEntity(board);
	      		board.userDetails = {
					avatar: "/services/js/usr/svc/user.js/$pics/"+board.user 
				};
				$UserImg.get(board.user)
				.then(function(image){
					if(!image)
						board.userDetails.avatar= undefined;
				});
				return board;
			});
		};
		var update = function(board){
			return Board.update(board).$promise
			.then(function(board){
	      		return formatEntity(board);
			});
		};	
		var remove = function(board){
			return Board.remove({boardId: board.id, cascaded:true}).$promise;
		};		
		var saveVote = function(board, v){
			return BoardVote.save({"boardId": board.id}, {"vote":v}).$promise
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
			return BoardTags.save({"boardId": board.id}, tags).$promise;
		};
		var untag = function(board, tags){
			return BoardTags.remove({"boardId": board.id}, tags).$promise;
		};
		var lock = function(board){
			board.locked = true;
			return Board.update({"boardId": board.id}, board).$promise;
		};		
		var unlock = function(board){
			board.locked = false;
			return Board.update({"boardId": board.id}, board).$promise;
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
	 		unlock:unlock
	 	};
	}])	
	.service('FilterList', [function() {
		var _filterText;
	  	return {
	  		filterText: _filterText
	  	};
	}]);
})(angular);	

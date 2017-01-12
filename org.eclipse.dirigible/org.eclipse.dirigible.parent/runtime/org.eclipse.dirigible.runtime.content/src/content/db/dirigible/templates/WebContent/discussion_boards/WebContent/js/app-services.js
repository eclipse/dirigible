(function(angular){
"use strict";
	angular.module('discussion-boards')
	.service('$DBoardVisits', ['BoardVisits', '$q', function(BoardVisits, $q) {
		var visited = [];
		var put = function(id){
			if(visited.indexOf(id)<0){
				visited.push(id);
				return BoardVisits.update({"boardId": id}, {}).$promise;
			} else {
				return $q.when(false);
			}
		};
	  	return {
	  		visit: put
	  	};
	}])	
	.service('$Boards', ['Board', 'BoardVote', 'BoardTags', '$UserImg', '$moment', function(Board, BoardVote, BoardTags, $UserImg, $moment) {
		
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
	      			comment.timeSincePublish = asElapsedTimeString(comment.publishTime);
					comment.publishTimeLocal = $moment(comment.publishTime).format('LLL'); 	      			
	      			if($moment(comment.lastModifiedTime).isAfter($moment(comment.publishTime))){
	      				comment.lastModifiedTimeLocal = $moment(comment.lastModifiedTime).format('LLL');
	      				comment.timeSinceLastModified = asElapsedTimeString(comment.lastModifiedTime);
      				}
	      			if(comment.replies){
	      				comment.replies = comment.replies.map(function(reply){
	          				reply.timeSincePublish = asElapsedTimeString(reply.publishTime);
	          				reply.publishTimeLocal = $moment(reply.publishTime).format('LLL');
	          				if($moment(reply.lastModifiedTime).isAfter($moment(reply.publishTime))){
	      						reply.lastModifiedTime = asElapsedTimeString(reply.lastModifiedTime);
	          					reply.lastModifiedTimeLocal = $moment(reply.lastModifiedTime).format('LLL');	      						
      						}
	          				return reply;
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
						avatar: "/services/js/idm/svc/user.js/$pics/"+board.user 
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
					avatar: "/services/js/idm/svc/user.js/$pics/"+board.user 
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
			.then(function(vote){
	      		return vote;
			});
		};
		var setTags = function(board, tags){
			return BoardTags.save({"boardId": board.id}, tags).$promise;
		};
		var untag = function(board, tags){
			return BoardTags.remove({"boardId": board.id}, tags).$promise;
		};		
	 	return {
	 		list: list,
	 		get :get,
	 		update: update,
	 		getVote: getVote,
	 		saveVote: saveVote,
	 		getTags: getTags,
	 		setTags: setTags,
	 		untag: untag
	 	};
	}])	
	.service('FilterList', [function() {
		var _filterText;
	  	return {
	  		filterText: _filterText
	  	};
	}])
})(angular);	

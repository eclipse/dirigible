var socket;
try {
	socket = new WebSocket(((location.protocol === 'https:') ? "wss://" : "ws://") 
			+ window.location.host + "/debugsessions");
} catch(e) {
	document.writeln("<div style='background-color: black; font-family: monospace; color: red'>[" + new Date().toISOString() + "][error]" + e.message + "</div>");
	
}
socket.onmessage = onMessage;

function onMessage(event) {
    var sessions = JSON.parse(event.data);
    $("#sessions").empty();
    printSessions(sessions);
}

function setBackgroundForActive(id){
	$('#'+id).css('background-color', 'lightgray');
	var lis = $('li').size();
	for(var i = 0; i<lis; i++){
		if(i != id){
			$('#'+i).css('background-color', '');
		}
	}
}

function setActiveSession(id){
	var sessionId = $('#'+id).text();
	var message = {
			"sessionId": sessionId
	}
	socket.send(JSON.stringify(message));
	setBackgroundForActive(id);
}

function printSessions(sessions){
	for(var i = 0; i<sessions.length; i++){
		var sessionId = sessions[i].sessionId;
		$("#sessions").append('<li onclick="setActiveSession(this.id)" id="'+i+'">'+sessionId+'</li>');
	}
}
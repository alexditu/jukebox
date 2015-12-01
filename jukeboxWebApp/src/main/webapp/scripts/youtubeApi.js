function testJS() {
	console.log('Inside testJS');
}

function mute() {
	  if (player.isMuted() == false) {
		  $('#bMute').innerHTML = 'Unmute';
		  player.mute();
	  } else {
		  $('#bMute').innerHTML = 'Mute';
		  player.unMute();
	  }
}

function callRestErrorHandler(jqXHR, textStatus, errorThrown) {
	console.log('textStatus: ' + textStatus + ' errorThrown: ' + errorThrown);
}

function doPostJson(methodName, data, succesCallBack) {
    console.log('doPostJson');
//    var data = {name: "Donald Duck", city: "Duckburg"};
    
    $.ajax({
	  url:'/juke/j/rest/' + methodName,
	  type: type,
	  data: JSON.stringify(data),
	  contentType:"application/json; charset=utf-8",
	  dataType:"json",
	  success: succesCallBack,
	  error: callRestErrorHandler
	  //, timeout: millis
	  
	});
    
}


function doPost(methodName, data, succesCallBack) {
//    var data = '';
    
//    console.log('dataMap: ' + typeof dataMap);
//    
//    if (dataMap.size > 0) {
////	    for (var i = 0, keys = Object.keys(dataMap), ii = keys.length; i < ii; i++) {
////	 		console.log('key : ' + keys[i] + ' val : ' + dataMap[keys[i]]);
////	 		data += keys[i] + '=' + dataMap[keys[i]] + '&';
////		}
//	    
////	    for (var [key, value] of dataMap.entries()) {
////	    	data += key + '=' + value + '&';
////	    }
//    	
//    	for (var key in dataMap) {
//    		data += key + '=' + dataMap[key] + '&';
//    	}
//	    data = data.substring(0, data.length - 1);
//    }
//    console.log('data: ' + data);
    
    $.ajax({
	  url:'/juke/j/rest/' + methodName,
	  type: 'POST',
	  data: data,
	  contentType:"application/x-www-form-urlencoded; charset=utf-8",
	  dataType:"html",
	  success: succesCallBack,
	  error: callRestErrorHandler
	  //, timeout: millis
	  
	});
    
}

/* loads the song after getSong returns */
function loadSongCallback(result, status, xhr) {
	var song = JSON.parse(result);
	player.loadVideoById({'videoId': song.id, 'startSeconds': song.seekTime});
}

/* first function called by YT API, inits the player */
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
      height: '390',
      width: '640',
      //videoId: 'Wha1sKumYQ4',
      events: {
        'onReady': onPlayerReady,
        'onStateChange': onPlayerStateChange
      }
    });
//    document.getElementById("player").style.visibility = "hidden";
}

function onPlayerReady(event) {
	console.log('onPlayerReady');
	doPost('getSong', '', loadSongCallback);
}

function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.ENDED) {
    	doPost('getSong', '', loadSongCallback);
    }
    
}

function hidePlayer() {
	$('#player').style.visibility = "hidden";
}

/* Test server sent events */
var evtSource;
function testSSE() {
	evtSource = new EventSource("/juke/j/rest/testSSE");
	console.log('SSE');
	
	evtSource.onmessage = function(e) {
		  console.log('HERE: ' + e);
		  console.log('event: ' + e.event);
		  console.log('data: ' + e.data);
		  console.log('id: ' + e.id);
		  console.log('retry: ' + e.retry + '\n');
		};
		
	evtSource.onopen = function (e) {
		 console.log("Waiting message..");
		 console.log(e);
	};
	
	evtSource.onerror = function(e) {
		  console.log('Error');
		  alert(e);
		};
		
	evtSource.addEventListener("msg", function(e) {
		console.log("MSG: " + e);
		console.log("source:" + e.toSource());
		console.log('event: ' + e.event);
		console.log('data: ' + e.data);
		console.log('id: ' + e.id);
		console.log('retry: ' + e.retry + '\n');
		}, false);
	
//	setTimeout(function(){ console.log('Closing connection'); evtSource.close(); }, 10000);
}

var evtBcastSource;
function bcastSSE() {
	evtBcastSource = new EventSource("/juke/j/rest/listenToBroadcast");
	console.log('SSE');
	
	evtBcastSource.onmessage = function(e) {
		  console.log('HERE: ' + e);
		  console.log('event: ' + e.event);
		  console.log('data: ' + e.data);
		  console.log('id: ' + e.id);
		  console.log('retry: ' + e.retry + '\n');
		};
		
	evtBcastSource.onopen = function (e) {
		 console.log("Waiting message..");
		 console.log(e);
	};
	
	evtBcastSource.onerror = function(e) {
		  console.log('Error');
	};
		
	evtBcastSource.addEventListener("msg", function(e) {
		console.log('event: ' + e.event);
		console.log('data: ' + e.data);
		console.log('id: ' + e.id);
		console.log('retry: ' + e.retry + '\n');
		}, false);
	
//	setTimeout(function(){ console.log('Closing connection'); evtSource.close(); }, 10000);
}

function closeSSE() {
	console.log('closing sse');
	evtSource.close();
}




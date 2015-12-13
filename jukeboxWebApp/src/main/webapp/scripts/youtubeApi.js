/* Load player */
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
var player;


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
    document.getElementById("player").style.visibility = "hidden";
//    hidePlayer();
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




/* Server Sent Events functions */
var evtSource;
function openSseConnection(username) {
	evtSource = new EventSource("/juke/j/rest/openSseConn?username=" + username);
	console.log('open conn for: ' + username);
	
	/* register callback for all messages */
/*	evtSource.onmessage = function(e) {
		  console.log('event: ' + e.event);
	};
*/
		
	evtSource.onopen = function (e) {
		 console.log("Waiting message..");
		 console.log(e);
	};
	
	evtSource.onerror = function(e) {
		  console.log('Error');
		  alert(e);
	};
		
	evtSource.addEventListener("addUser", addUserCallback, false);
	evtSource.addEventListener("removeUser", removeUserCallback, false);
	evtSource.addEventListener("updateSong", updateSongVotesCallback, false);
	evtSource.addEventListener("changeSong", updateCurrentSong, false);
	evtSource.addEventListener("updatePower", updatePower, false);

	
//	setTimeout(function(){ console.log('Closing connection'); evtSource.close(); }, 10000);
}

function closeSseConnection() {
	console.log('closing sse');
	evtSource.close();
}

/* Update connected users */
function addUserCallback(evt) {
	displayUsers();
	console.log('new user: ' + evt.data);
}

/* update current song */
function updateCurrentSong(evt) {
	setCurrentSong();
	displayUserPower()
}

/* update power */
function updateCPower(evt) {
	displayUserPower()
}

function removeUserCallback(evt) {
	displayUsers();
	displaySongs(scenario_var);
	console.log('remove user: ' + evt.data);
}

/* Update songs votes */
function updateSongVotesCallback(evt) {
	displaySongs(scenario_var);
	console.log('updateSongVotes: ' + evt.data);
}











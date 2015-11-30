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
    console.log('doPost');
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





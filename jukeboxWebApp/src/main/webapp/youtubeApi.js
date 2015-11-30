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

function callRest(methodName, type, succesCallBack) {
    console.log('aloha2');
    var data = {name: "Donald Duck", city: "Duckburg"};
    
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


function getSong() {
	console.log('Get Song:');
	callRest('getSong', 'POST', function(result, status, xhr) {
		console.log('result: ' + JSON.stringify(result) + ' status: ' + status);
	});
}

function getTime() {
	console.log('Get Time:');
	callRest('getTime', 'POST', function(result, status, xhr) {
		console.log('result: ' + JSON.stringify(result) + ' status: ' + status);
		console.log('seektime: ' + result.seekTime);
		return result;
	});
	
}





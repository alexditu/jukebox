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

function doPost(methodName, dataMap, succesCallBack) {
    console.log('doPost');
    var data = '';
    
    //for (var [key, value] of dataMap.entries()) {
    //	data += key + '=' + value + '&';
    //}
    for (var i = 0, keys = Object.keys(dataMap), ii = keys.length; i < ii; i++) {
 		console.log('key : ' + keys[i] + ' val : ' + dataMap[keys[i]]);
 		data += keys[i] + '=' + dataMap[keys[i]] + '&';
	}
    data = data.substring(0, data.length-1);
    console.log('data: ' + data);
    
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

function addUser(username) {
	console.log('Add user: ' + username);
	
	var map = new Map();
	map.set("username", username);
	
	doPost('addUser', map, function(result, status, xhr) {
		console.log('result: ' + result.toString() + ' status: ' + status);
	});
}

function getSongs() {
	console.log('Get Songs:');
	
	var map = new Map();
	map.set("username", "alex");
	
	doPost('getSongs', map, function(result, status, xhr) {
		console.log('result: ' + result.toString() + ' status: ' + status);
	});
}

function getUsers() {
}


function getSong() {
	console.log('Get Song:');
	var song;
	callRest('getSong', 'POST', function(result, status, xhr) {
		console.log('result: ' + JSON.stringify(result) + ' status: ' + status);
		song = result;
	});
	return song;
}

function getTime() {
	console.log('Get Time:');
	callRest('getTime', 'POST', function(result, status, xhr) {
		console.log('result: ' + JSON.stringify(result) + ' status: ' + status);
		console.log('seektime: ' + result.seekTime);
		return result;
	});
	
}





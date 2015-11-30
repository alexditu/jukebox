window.onload = loadVariables;

function loadVariables() {
	username = sessionStorage.getItem('username');
	document.getElementById("username").innerHTML = username;
	console.log("Username is " + username);

	// load user list
	var activeUsers = {
	    users: [{
	            username: "Aliasghar"
	            , grade: 19
	        }, {
	            username: "Afagh"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20
	        }, {
	            username: "gogu"
	            , grade: 20

	    }]
	}
	$.get('templates/userList.html', function(template) {
		var rendered = Mustache.render(template, activeUsers);
	    $('#activeUsers').html(rendered);
	});

}
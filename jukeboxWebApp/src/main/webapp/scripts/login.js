document.getElementById("loginButton").addEventListener("click", userLogin);

$(document).ready(function(){
   		console.log("Got a here");

   $("#userName").keyup(function(event){
   		console.log("Got here");
    	if(event.keyCode == 13){
        	$("#loginButton").click();
        	console.log("Got here");
    	}
	});
});


function userLogin() {
    name = document.getElementById('userName').value;
    if (name.length == 0) {
    	window.alert("Please provide a username");
    } else {
    	//trimit la alex 
    	addUser(name);
    	sessionStorage.setItem('username', name);
    	window.location.assign("/web/index.html");
    }
}
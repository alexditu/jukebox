document.getElementById("loginButton").addEventListener("click", userLogin);

$(document).ready(function(){

   $("#userName").keyup(function(event){
        console.log("Got here");
        if(event.keyCode == 13){
            $("#loginButton").click();
            console.log("Got here");
        }
    });
});

function addUser(username) {
    var params = "username=" + username;

    doPost('addUser', params, function(result, status, xhr) {
        result = JSON.parse(result);

        if (result.status == "EXISTS") {
            window.alert("User already exists. Please choose a different username");
        } else {
            sessionStorage.setItem('username', name);
            window.location.assign("/web/index.html");
        }
    });
}

function userLogin() {
    name = document.getElementById('userName').value;
    if (name.length == 0) {
        window.alert("Please provide a username");
    } else {
        addUser(name);
    }
}
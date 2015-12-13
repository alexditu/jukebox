var username, songList;
window.onload = loadVariables;
document.getElementById("tab_recent").addEventListener("click", function(){ displaySongs("recent"); });
document.getElementById("tab_trending").addEventListener("click", function(){ displaySongs("trending"); });
document.getElementById("tab_all").addEventListener("click", function(){ displaySongs("all"); });
document.getElementById("logout").addEventListener("click", function(){ logout(); });

$(document).ready(function(){

   $("#liveSearch").keyup(function(event){
	   
    });
});

function logout() {
    var param = "username=" + username;
    doPost("removeUser", param, function(result, status, xhr) {
    	console.log('logut result: ' + result);
    	/* must be after function finishes execution otherwise connection is cut */
    	window.location.assign("/web/login.html");
    });
    
}

function checkSong(author) {
    var param = "username=" + username + "&songID=" + author.id + "&checked=" + author.checked;
    doPost("checkSong", param, function(result, status, xhr) {});
}

function displaySongs(scenario) {
    var param = "username=" + username;

    //console.log("Here " + scenario);

    doPost('getSongs', param, function(result, status, xhr) {
        //console.log(result.toString())
        result = JSON.parse(result);
        if (scenario == "recent") {
            result.songs.sort(function(a,b){
                if(a.age == b.age)
                    return 0;
                if(a.age < b.age)
                    return 1;
                if(a.age > b.age)
                    return -1;
            });
        } else if (scenario == "trending") {
            result.songs.sort(function(a,b){
                if(a.score == b.score)
                    return 0;
                if(a.score < b.score)
                    return 1;
                if(a.score > b.score)
                    return -1;
            });
        }
        songList = result;
        var dest = '#' + scenario;
        $.get('templates/songList.html', function(template) {
            var rendered = Mustache.render(template, result);
            $(dest).html(rendered);
        });
    });
}

function displayUsers() {
    params = "username=" + username;

    doPost('getUsers', params, function(result, status, xhr) {
        $.get('templates/userList.html', function(template) {
            result = JSON.parse(result);
            var rendered = Mustache.render(template, result);
            $('#activeUsers').html(rendered);
        });
    });
}

function follow(user) {
    console.log(user);
    params = "username=" + username + "&followedUser=" + user;
    doPost('followUser', params, function(result, status, xhr) {
        result = String(result);
        if (result == "follow") {
            document.getElementById(user).innerHTML = "Unfollow";
        } else {
            document.getElementById(user).innerHTML = "Follow";
        }
    });
}

function displayUserPower() {
    params = "username=" + username;
    doPost("getPower", params, function(result, status, xhr) {
        result = JSON.parse(result);
        document.getElementById("userPower").innerHTML = "Voting Power: " + result.power;
    });
}

function loadVariables() {
    username = sessionStorage.getItem('username');
    document.getElementById("username").innerHTML = username + "<span class=\"caret\"></span>";
    //console.log("Username is " + username);

    displayUsers();
    displaySongs("recent");
    displayUserPower();

    setCurrentSong();
    openSseConnection(username);
}

function setCurrentSong() {
    doPost("getSong", "", function(result, status, xhr) {
        //console.log("Requesting current song: " + result);
        result = JSON.parse(result);
        document.getElementById("currentSong").innerHTML = "Now playing:    " + result.artist + " - " + result.name;
    });
}
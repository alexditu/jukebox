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
    doPost("removeUser", param, function(result, status, xhr) {});
    window.location.assign("/web/login.html");
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
    doPost('getUsers', "", function(result, status, xhr) {
        $.get('templates/userList.html', function(template) {
            result = JSON.parse(result);
            var rendered = Mustache.render(template, result);
            $('#activeUsers').html(rendered);
        });
    });
}

function loadVariables() {
    username = sessionStorage.getItem('username');
    document.getElementById("username").innerHTML = username + "<span class=\"caret\"></span>";
    //console.log("Username is " + username);

    displayUsers();
    displaySongs("recent");

    setCurrentSong();
}

function setCurrentSong() {
    console.log("Got hereeeee");
    doPost("getSong", "", function(result, status, xhr) {
        console.log("Requesting current song: " + result);
        result = JSON.parse(result);
        document.getElementById("currentSong").innerHTML = "Now playing:    " + result.artist + " - " + result.name;
    });
}
var username;
window.onload = loadVariables;
document.getElementById("recent").addEventListener("click", function(){ displayRecentSongs("recent"); });
document.getElementById("trending").addEventListener("click", function(){ displayRecentSongs("trending"); });
document.getElementById("all").addEventListener("click", function(){ displayRecentSongs("all"); });

function displaySongs(scenario) {
    var param = "username=" + "Alice";

    doPost('getSongs', map, function(result, status, xhr) {
        console.log(result.toString())
        result = JSON.parse(result);
        if (scenario == "recent") {
            result.sort(function(a,b){
                if(a.age == B.age)
                    return 0;
                if(a.age < B.age)
                    return 1;
                if(a.age > B.age)
                    return -1;
            });
        } else if (scenario == "trending") {
            result.sort(function(a,b){
                if(a.score == B.score)
                    return 0;
                if(a.score < B.score)
                    return 1;
                if(a.score > B.score)
                    return -1;
            });
        }
        $.get('templates/songList.html', function(template) {
            var rendered = Mustache.render(template, result);
            $('#recent').html(rendered);
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
    document.getElementById("username").innerHTML = username;
    console.log("Username is " + username);

    displayUsers();
    displaySongs("recent");
}
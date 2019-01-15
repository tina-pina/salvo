
let url = '/api/games';
fetch(url).then(response => response.json()).then(function(myJson) {
    createList(myJson);
});


function createList(jsonArr) {
    for(let obj of jsonArr) {
        var gamesList = document.getElementById('gamesList');
        var entry = document.createElement('li');
        entry.innerHTML = JSON.stringify(obj);
        gamesList.appendChild(entry);
    }
}





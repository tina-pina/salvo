

function paramObj(search) {
  var obj = {};
  var keyRes = " ";
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  for(let key in obj) {
    keyRes += obj.gp
  }

  return keyRes;
}


let gamePlayerId = paramObj(location.href);
console.log(gamePlayerId)

let url = (`/api/game_view/${gamePlayerId}`)

fetch(url).then(res => res.json())
.then(json => {
    let playerEmailArr = [];
    let shipsArr = json.ships;
    let gamePlayers = json.gamePlayers;
    for(let player of gamePlayers) {
//        console.log(player.player.email);
        if(!playerEmailArr.includes(player.player.email)) {
           playerEmailArr.push(player.player.email);
        }
    }
//    console.log(playerEmailArr);

    for(let item of playerEmailArr) {
        console.log(item);
        let players = document.getElementById("players");
        let p = document.createElement("p");
        p.innerHTML = item;
        players.appendChild(p);
    }

    let shipLocations = []
    for(let ship of shipsArr) {
        for(let loc of ship.locations) shipLocations.push(loc);
    }
    console.log(shipLocations);
    for(let locationCode of shipLocations) {
        let targetGrid = locationCode;
        console.log(targetGrid);
        let tableId = document.querySelectorAll(`#${targetGrid}`);
        let singleElement = tableId[0];
        //todo give each ship a different color;
        singleElement.style.backgroundColor = 'yellow';
    }
})


let grid = document.getElementById("grid");
let table = document.createElement("table");
let colNames = " ABCDEFGHIJ";

for(let i=0; i< 11; i++) {
    let row = document.createElement("tr");
    for(let j=0; j<11; j++) {
        let col = document.createElement("td");
        if(i === 0 && j > 0) col.innerHTML = `${j}`;  // first row
        if(j === 0 && i > 0) col.innerHTML = colNames[i];  // first item
        col.id = colNames[i] + j;
        row.appendChild(col);
    }
    table.appendChild(row);
}
grid.appendChild(table);


















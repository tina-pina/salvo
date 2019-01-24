


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

//get the game player id from the sites url


function updateGrids() {
//http://localhost:8080/api/game_view/

    let gamePlayerId = paramObj(location.href);
     console.log(gamePlayerId)

    let url = (`/api/game_view/${gamePlayerId}`)

    fetch(url).then(res => res.json())
    .then(json => {

        let playerEmailArr = [];
        let shipsArr = json.ships;
        let salvosArr = json.salvos;

        let gamePlayers = json.gamePlayers;
        for(let player of gamePlayers) {
            if(!playerEmailArr.includes(player.player.email)) {
               playerEmailArr.push(player.player.email);
            }
        }

        for(let item of playerEmailArr) {
            let players = document.getElementById("players");
            let p = document.createElement("p");
            p.innerHTML = item;
            players.appendChild(p);
        }

        //add the ships on their designated locations to the grid;

        let shipLocations = [];
        for(let ship of shipsArr) {
            for(let loc of ship.locations) shipLocations.push(loc);
        }
        for(let locationCode of shipLocations) {
            let targetGrid = locationCode;
            let tableId = document.querySelectorAll(`#shipsGrid #${targetGrid}`);
            let singleElement = tableId[0];
            //todo give each ship a different color;
            singleElement.style.backgroundColor = 'yellow';
        }

        let playerSalvo = {};
        let opponentSalvo = {}

        for(let salvo of salvosArr) {
            if(salvo.player === Number(gamePlayerId)) {
                playerSalvo[salvo.turn] = salvo.locations
            }
            else {
                opponentSalvo[salvo.turn] = salvo.locations
            }
        }

        for(turnKey in playerSalvo) {
            let locations = playerSalvo[turnKey]
            for(let locCode of locations) {
                let tableId = document.querySelectorAll(`#shipsGrid #${locCode}`);
                let singleElement = tableId[0];
                singleElement.style.backgroundColor = 'red';
                singleElement.innerHTML = turnKey;
            }
        }

        for(turnKey in opponentSalvo) {
            let locations = opponentSalvo[turnKey]
            for(let locCode of locations) {
                let tableId = document.querySelectorAll(`#salvosGrid #${locCode}`);
                let singleElement = tableId[0];
                singleElement.style.backgroundColor = 'grey';
                singleElement.innerHTML = turnKey;
            }
        }
    })

}

//
//const testRequest = async () => {
//    let gamePlayerId = paramObj(location.href);
//    const response = await fetch(`/api/games/players/${gamePlayerId}/ships`, {
//          headers: {
//            'Accept': 'application/json',
//            'Content-Type': 'application/json'
//          },
//          method: "POST",
//          body: JSON.stringify(
//           [{ "type": "destroyer", "locations": ["A1", "B1", "C1"]},
//             { "type": "patrol boat", "locations": ["H6", "H7"] },
//             { "type": "Aircraft Carrier", "locations": ["J1", "J2", "J3", "J4", "J5"]}
//           ]
//          )
//    });
//    const json = await response.json();
//    console.log(json);
//}


//function createShips(gamePlayerId, shipArr) {
//
//    let gamePlayerId = paramObj(location.href);
//    const response = await fetch(`/api/games/players/${gamePlayerId}/ships`, {
//          headers: {
//            'Accept': 'application/json',
//            'Content-Type': 'application/json'
//          },
//          method: "POST",
//          body: JSON.stringify(shipArr)
//    });
//    const json = await response.json();
//    console.log(json);
//
//}



function createGrids() {

    //add ships grid

    let grid = document.getElementById("shipsGrid");
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

    //add salvos grid

    grid = document.getElementById("salvosGrid");
    table = document.createElement("table");
    colNames = " ABCDEFGHIJ";

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

}

createGrids();
updateGrids();



document.getElementById("submit-btn-logout").addEventListener("click", function(e){

    e.preventDefault();

    $.post("/api/logout")
    .done(function() {
        window.location = "/web/games.html"
    })
    .catch(error => {})
})


//$( "#testDrag" ).draggable();
//$( "#B3" ).droppable({
//  drop: function( event, ui ) {
//    console.log('dropeed')
//  }
//});















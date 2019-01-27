


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

    let table = document.getElementById("shipsGrid");
    let colNames = " ABCDEFGHIJ";

    for(let i=0; i< 11; i++) {
        let row = document.createElement("tr");
        for(let j=0; j<11; j++) {
            let col = document.createElement("td");
            if(i === 0 && j === 0) col.innerHTML = ""
            else if(i === 0 && j > 0) col.innerHTML = `${j}`;  // first row
            else if(j === 0 && i > 0) col.innerHTML = colNames[i];  // first item
            else {
                col.addEventListener('dragover', function(event){
                    allowDrop(event)
                    event.target.style.borderColor = "red";
                    event.target.style.borderWidth = "thick";
                })
                col.addEventListener('drop', function(event){
                    drop(event) ;
                    event.target.style.borderColor = "#dddddd";
                    event.target.style.borderWidth = "1px";
                })
                col.addEventListener('dragleave', function(event){
                    event.target.style.borderColor = "#dddddd";
                    event.target.style.borderWidth = "1px";
                })
            }
            col.id = colNames[i] + j;
            row.appendChild(col);
        }
        table.appendChild(row);
    }

    //add salvos grid
    table = document.getElementById("salvosGrid");
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



//drag/ drop part
let gridArr = generateGridArray()


function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();

    /* get element you drag */
    var data = ev.dataTransfer.getData("text");

    /* append element to where you drop */
    ev.target.appendChild(document.getElementById(data));

    /* get grid id and grid DOM */
    let gridId = ev.srcElement.id;
    let targetDom = document.getElementById(gridId);

    /* get ship id / ship type */
    let shipLength = createShipsLength(targetDom.firstChild.id);

    /* generate ship locations */
    let shipLocations = generateShipLoc(gridId, shipLength, shipDirection = "horizontal")

    /* update original gridArr */
    let updatedGrid =  updateShipLoc(shipLocations, gridArr);

    /* reflect the color */
    let displayShips = colorGrid(gridArr);

    /* remove ship */
    targetDom.firstChild.remove();

}

function createShipsLength(shipType) {
    if(shipType === "dragP") return 2;
    if(shipType === "dragS" || shipType === "dragD") return 3;
    if(shipType === "dragB") return 4;
    else return 5;
}

function generateGridArray() {
    let grArr = [];

    for(let i = 0; i< 10; i++) {
        let rowArr = [];
        for(let j = 0; j<10; j++) {
            rowArr.push(false)
        }
        grArr.push(rowArr);
    }
    return grArr;
}

function updateGrid(rowId, colId, gridArr) {
    let colNum = Number(colId) - 1;

    if(!["A","B","C","D","E","F","G","H","I","J"].includes(rowId)) return ["row ID is WRONG!"]
    if(!["1","2","3","4","5","6","7","8","9","10"].includes(colId)) return ["col ID is WRONG!"]

    if(rowId === "A") gridArr[0][colNum] = true;
    else if(rowId === "B") gridArr[1][colNum] = true;
    else if(rowId === "C") gridArr[2][colNum] = true;
    else if(rowId === "D") gridArr[3][colNum] = true;
    else if(rowId === "E") gridArr[4][colNum] = true;
    else if(rowId === "F") gridArr[5][colNum] = true;
    else if(rowId === "G") gridArr[6][colNum] = true;
    else if(rowId === "H") gridArr[7][colNum] = true;
    else if(rowId === "I") gridArr[8][colNum] = true;
    else if(rowId === "J") gridArr[9][colNum] = true;
    return gridArr;
}

function updateShipLoc(shipLocArr, gridArr) {

    for(let loc of shipLocArr) {
        let rowId = loc[0];
        let colId = loc.slice(1);

        gridArr = updateGrid(rowId, colId, gridArr);
    }
    return gridArr;
}

function generateShipLoc(start, shipLength, shipDirection = "horizontal") {

    let rowId = start[0]
    let colId = start.slice(1);
    let shipLocArr = [];

    if(shipDirection === 'horizontal') {
        for(let i=0; i<shipLength; i++) {
            let shipLoc = rowId + (Number(colId) + i);
            shipLocArr.push(shipLoc)
        }
    }

    else if(shipDirection === 'vertical') {
        //A1, B1, C1
        let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
        let currentRowIndex = rowArr.indexOf(rowId) //2
        for(let i=0; i<shipLength; i++) {
            let shipLoc = rowArr[currentRowIndex + i] + colId;
            shipLocArr.push(shipLoc);
        }
    }
    return shipLocArr;
}

function colorGrid(gridArr) {
    let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    for(let i=0; i<gridArr.length;i++) {
        for(let j=0; j<gridArr.length; j++) {
            let columnId = j+1;
            let rowId = rowArr[i];
            let finalId = rowId + columnId
            if(gridArr[i][j] === true) {
               console.log(finalId)
               document.getElementById(finalId).style.backgroundColor = "blue";
            }
            else {
               document.getElementById(finalId).style.backgroundColor = "white";

            }
        }
    }
}
























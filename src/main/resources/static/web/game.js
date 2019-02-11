
/* drag/ drop part */
let shipGridArr = generateGridArray();
let salvoGridArr = generateGridArray();
let shipData = [];
let salvoLocArr = [];
let currentTurn = 1;

/* display grid */
createGrids();

/* polling game state */
getGameView();
setInterval(getGameView, 2000);


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
  return keyRes.trim();
}


function getGameView(){

    let gamePlayerId = paramObj(location.href)
    fetch(`/api/game_view/${gamePlayerId}`)
    .then(response => response.json())
    .then(json => {

        console.log(json);
        /* manage ui based on game state */
        manageUI(json.gameState.state)

        /* updating turn */
        currentTurn = json.turn;

        /* Game State Message */
        let gameState = document.getElementById("gameState");
        gameState.innerHTML = json.gameState.message;

        /* show players for the game on top of the page*/
        let gamePlayers = json.gamePlayers;
        showGamePlayers(gamePlayers)

        /* add the ships on their designated locations to the grid */
        /* ships data */
        let shipsArr = json.ships;
        shipGridArr = createShipGrid(shipsArr)
        colorGrid(shipGridArr, "ship")

        /* salvos data */
        let salvosArr = json.salvos;
        /* add salvos on their designated locations to the grid */
        createSalvoGrid(salvosArr)

        /* update hist board */
        createHistoryBoard(json)

    })
}

function manageUI(stateNo) {
    let shipSalvo = document.querySelector(".containerShipsSalvos");
    let ships = document.querySelector(".containerShips");
    let salvoBtn = document.getElementById("submit-btn-salvos")
    let shipParkingTitle = document.querySelector(".shipParkingTitle");

    if(stateNo === 0) {
       shipSalvo.style.display = "none";

    } else  {
       shipSalvo.style.display = "block";
    }

    if(stateNo === 1) {
       ships.style.display = "block";
    } else  {
       ships.style.display = "none";
    }

    if(stateNo === 6) {
       salvoBtn.style.display = "block";
    }
     else  {
       salvoBtn.style.display = "none";
    }
    if(stateNo === 8 ) {
        shipParkingTitle.style.display = "none";
    }
}

function createHistoryBoard(json) {
    let table = document.getElementById("hitsSinks");

//    get all removable rowS -> remove
    let removeRows = document.querySelectorAll(".removableRow");
    for(let row of removeRows) row.remove();

    for(let turn = 1; turn < json.turn; turn++) {
        let row = document.createElement("tr");
        row.className = "removableRow"

        /* turn */
        let col0 = document.createElement("td");
        col0.innerHTML = turn;
        row.appendChild(col0);

        /* opponent hits */
        let col1 = document.createElement("td");
        let hitsString = "";
        for(let shipType in json.opponentAttackHistory[turn]) {
            if(json.opponentAttackHistory[turn][shipType].hits !== 0) {
                hitsString += shipType + " (" + json.opponentAttackHistory[turn][shipType].hits + ")/";
            }
        }
        if(hitsString.length === 0) hitsString = "No hits!"
        col1.innerHTML = hitsString;
        row.appendChild(col1);

        /* your own ships left*/
        let col2 = document.createElement("td");
        col2.innerHTML = json.yourShipsLeft[turn];
        row.appendChild(col2);

        /* your own hits */
        let col3 = document.createElement("td");
        let hitsStringOpp = "";
        for(let shipType in json.yourAttackHistory[turn]) {
            if(json.yourAttackHistory[turn][shipType].hits !== 0) {
                hitsStringOpp += shipType + " (" + json.yourAttackHistory[turn][shipType].hits + ")/";
            }
        }
        if(hitsStringOpp.length === 0) hitsStringOpp = "No hits!"
        col3.innerHTML = hitsStringOpp;
        row.appendChild(col3);

        /* opponents ships left*/
        let col4 = document.createElement("td");
        col4.innerHTML = json.opponentShipsLeft[turn];
        row.appendChild(col4);
        table.appendChild(row);

    }






}

/* get the game player id from the sites url */

function showGamePlayers(gamePlayers) {

    let playerEmailArr = [];
    for(let player of gamePlayers) {
        if(!playerEmailArr.includes(player.player.email)) {
           playerEmailArr.push(player.player.email);
        }
    }

    /* show players for the game on top of the page*/
    playerEmailArr.sort();
    let player1 = document.getElementById("player1");
    let player2 = document.getElementById("player2");

    player1.innerHTML = playerEmailArr[0];

    if(!playerEmailArr[1])  player2.innerHTML = "Waiting for another player to join the game!";
    else player2.innerHTML = playerEmailArr[1];
}






function createShipGrid(shipsArr) {

    let shipLocations = [];
    for(let ship of shipsArr) {
        for(let loc of ship.locations) shipLocations.push(loc);
    }
    for(let locationCode of shipLocations) {
        let rowId = locationCode[0];
        let colId = locationCode.slice(1);
        //after creating the basic array it is updated -> updateGrid func
        shipGridArr = updateGrid(rowId, colId, shipGridArr);
    }

    return shipGridArr

}

function createSalvoGrid(salvosArr) {
    let gamePlayerId = Number(paramObj(location.href));
    let playerSalvo = salvosArr.filter(s => s.gamePlayerId === gamePlayerId)[0].salvos
    let opponentSalvo = salvosArr.filter(s => s.gamePlayerId !== gamePlayerId)[0].salvos

    for(let turnKey in playerSalvo) {
        let locations = playerSalvo[turnKey] //"1", "2".....
        for(let locCode of locations) {
            let tableId = document.querySelectorAll(`#salvosGrid #${locCode}`); //A1, B2
            let singleElement = tableId[0];
            singleElement.style.backgroundColor = 'red';
            singleElement.innerHTML = turnKey;
        }
    }

    for(let turnKey in opponentSalvo) {
        let locations = opponentSalvo[turnKey]
        for(let locCode of locations) {
            let tableId = document.querySelectorAll(`#shipsGrid #${locCode}`);
            let singleElement = tableId[0];
            singleElement.style.backgroundColor = 'grey';
            singleElement.innerHTML = turnKey;
        }
    }
}


function createShips(shipArr) {

    let gamePlayerId = paramObj(location.href);
    fetch(`/api/games/players/${gamePlayerId}/ships`, {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          method: "POST",
          body: JSON.stringify(shipArr)
    }).then(function(res) {console.log(res)})
    .catch(function(res) {console.log(res)})
}


function createSalvos(salvoLocArr) {

    let gamePlayerId = paramObj(location.href);
    fetch(`/api/games/players/${gamePlayerId}/salvos`, {
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          method: "POST",
          body: JSON.stringify({ turnNum: currentTurn, locations: salvoLocArr })
    }).then(function(res) {console.log(res)})
    .catch(function(res) {console.log(res)})

}


function getShipName(shipType) {
    if(shipType === "dragP") return "PatrolBoat";
    if(shipType === "dragS") return "SubmarineBoat";
    if(shipType === "dragB") return "Battleship";
    if(shipType === "dragA") return "AircraftCarrier";
    if(shipType === "dragD") return "Destroyer";
}


function updateShipArray(shipType, shipLocations) {
    let obj = {};

//    { "type": "destroyer", "locations": ["A1", "B1", "C1"]}

    let shipName = getShipName(shipType);

    obj["type"] = shipName;
    obj["locations"] = shipLocations

    shipData.push(obj);
}


function createGrids() {

    let table = document.getElementById("shipsGrid");
    let colNames = " ABCDEFGHIJ";

    for(let i=0; i< 11; i++) {
        let row = document.createElement("tr");
        for(let j=0; j<11; j++) {
            let col = document.createElement("td");
            col.style.backgroundColor = "white";
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
                    drop(event);
                    event.target.style.borderColor = "blue";
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
            col.style.backgroundColor = "white";
            if(i === 0 && j > 0) col.innerHTML = `${j}`;  // first row
            if(j === 0 && i > 0) col.innerHTML = colNames[i];  // first item
            col.id = colNames[i] + j;

            col.addEventListener('click', clickSalvos)
            row.appendChild(col);
        }
        table.appendChild(row);
    }

}

function placeSalvo(event) {
    clickSalvos(event);
}

document.getElementById("submit-btn-ships").addEventListener("click", function(e){
    e.preventDefault();
    createShips(shipData);
    location.reload();
})


document.getElementById("submit-btn-logout").addEventListener("click", function(e){

    e.preventDefault();

    $.post("/api/logout")
    .done(function() {
        window.location = "/web/games.html"
    })
    .catch(error => {})
})


document.getElementById("submit-btn-salvos").addEventListener("click", function(e){
    e.preventDefault();


    if(salvoLocArr.length === 5) createSalvos(salvoLocArr);
    else alert('you can maximum submit 5 salvos!');
    salvoLocArr = [];
    location.reload();

})

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);

}

function drop(ev) {
    ev.preventDefault();

    /* get element you drag */
    let shipType = ev.dataTransfer.getData("text");

    /* parking location of ship */
    let shipTypeParking = document.getElementById(shipType).parentNode.id;

    /* append element to where you drop */
    ev.target.appendChild(document.getElementById(shipType));

    /* get grid id and grid DOM */
    let gridId = ev.srcElement.id;
    let targetDom = document.getElementById(gridId);

    /* get ship length */
    let shipLength = createShipsLength(shipType);

    /* get ship direction */
    let shipDirection = getShipDirection(shipType)

    /* generate ship locations */
    let shipLocations = generateShipLoc(gridId, shipLength, shipDirection)


    if(isShipLocInsideGrid(shipLocations) && isShipNotOverlap(shipLocations, shipGridArr)) {

        /* when user press submit ships button we fetch ships in the api*/
        let shipsToCreate = updateShipArray(shipType, shipLocations);

        /* update original gridArr */
        let updatedGrid =  updateMultipleLoc(shipLocations, shipGridArr);

        /* reflect the color */
        let displayShips = colorGrid(shipGridArr, "ship");

        /* remove ship */
        targetDom.firstChild.remove();

    } else {
        alert("ships should NOT overlap & ships should NOT go outside of the grid!")

        let shipT = document.getElementById(shipType);

        /* get the location where the ship was parked before */
        let shipP = document.getElementById(shipTypeParking);

        /* get ship type itself and clone it */
        let clnChild = shipT.cloneNode(true);

        /* remove ship and color of grid */
        targetDom.firstChild.remove();
        targetDom.style.borderColor = "#dddddd";
        targetDom.style.borderWidth = "1px";

        /* bring ship back to original location */
        shipP.appendChild(clnChild);

    }
}

function clickSalvos(ev) {
    let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

    let gridId = ev.srcElement.id;

    let rowId = gridId[0];
    let colId = gridId.slice(1);
    // get location of salvo on the grid
    let location = gridId;


    // check value salvosGridArr
    let rowIdx = rowArr.indexOf(rowId);

    if(salvoGridArr[rowIdx][colId-1] === true) {

        salvoGridArr[rowIdx][colId-1] = false;

        //remove salvo location from salvoLocArr

        for(let salvo of salvoLocArr) {
            if(salvo === location) {
                let indexLocInArray = salvoLocArr.indexOf(gridId);

                console.log("HERE", indexLocInArray);
                if(indexLocInArray > -1) {
                    salvoLocArr.splice(indexLocInArray, 1);
                }
            }
        }

        // color the salvo grid

        colorGrid(salvoGridArr, "salvo");
    }

    else {

        // update the salvo grid
        let salvoGrid = updateGrid(rowId, colId, salvoGridArr);
        salvoLocArr.push(location);

        // color the salvo grid
        colorGrid(salvoGridArr, "salvo");
    }
}


/* create ships lengths */
function createShipsLength(shipType) {
    if(shipType === "dragP") return 2;
    if(shipType === "dragS" || shipType === "dragD") return 3;
    if(shipType === "dragB") return 4;
    else return 5;
}


/* generate grid Arr for ships and salvos */
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

function updateMultipleLoc(locArr, gridArr) {

    for(let loc of locArr) {
        let rowId = loc[0];
        let colId = loc.slice(1);

        gridArr = updateGrid(rowId, colId, gridArr);
    }
    return gridArr;
}

/* generate ship location */
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
        let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
        let currentRowIndex = rowArr.indexOf(rowId) //2
        for(let i=0; i<shipLength; i++) {
            let shipLoc = rowArr[currentRowIndex + i] + colId;
            shipLocArr.push(shipLoc);
        }
    }
    return shipLocArr;
}

/* generate salvo location */


function colorGrid(gridArr, gridType) {

    let targetGrid;
    if(gridType === "ship") {
        targetGrid = "shipsGrid"
    } else if (gridType === "salvo") {
        targetGrid = "salvosGrid"
    }

    let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    for(let i=0; i<gridArr.length;i++) {
        for(let j=0; j<gridArr.length; j++) {
            let columnId = j+1;
            let rowId = rowArr[i];
            let finalId = rowId + columnId
            if(gridArr[i][j] === true) {
               document.querySelector(`#${targetGrid} #${finalId}`).style.backgroundColor = "blue";
               document.querySelector(`#${targetGrid} #${finalId}`).style.borderColor = "blue";
               document.querySelector(`#${targetGrid} #${finalId}`).style.borderWidth = "1px";
            }
            else if (gridArr[i][j] === false) {
                document.querySelector(`#${targetGrid} #${finalId}`).style.backgroundColor = "white";
                document.querySelector(`#${targetGrid} #${finalId}`).style.borderColor = "black";
                document.querySelector(`#${targetGrid} #${finalId}`).style.borderWidth = "1px";
            }
        }
    }
}

function isShipLocInsideGrid(shipLocations) {
    let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    let colArr = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"];
    for(let loc of shipLocations) {
        let row = loc[0];
        let col = loc.slice(1);
        if(!rowArr.includes(row) || !colArr.includes(col)) {
            return false;
        }
    }
    return true;
}

function isShipNotOverlap(shipLocations, gridArr) {
   /* if locations are already true -> already ship there -> return false */
   /* iterate the grid array if there is false or true inside */

   let rowArr = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
   for(let loc of shipLocations) {
        let rowId = loc[0]; //"H"
        let colIdx = loc.slice(1); //"4"
        let rowIdx = rowArr.indexOf(rowId);
        if(gridArr[rowIdx][colIdx-1] === true) {
            return false;
        }
   }
   return true;
}

function getShipDirection(shipType) {
    if(shipType === "dragP") return document.querySelector('input[name="directionP"]:checked').value;
    if(shipType === "dragS") return document.querySelector('input[name="directionS"]:checked').value;
    if(shipType === "dragD") return document.querySelector('input[name="directionD"]:checked').value;
    if(shipType === "dragB") return document.querySelector('input[name="directionB"]:checked').value;
    else return document.querySelector('input[name="directionA"]:checked').value;
}

/* create sample JSON Hits and Sinks */
// get data from backend for the hits and sinks table

function getHitsSinksData() {
    let gamePlayerId = paramObj(location.href);

    return fetch(`/api/game_view/${gamePlayerId}`)
      .then(response => response.json())
}











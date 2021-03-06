

function fetchGameData() {
    return fetch("/api/games")
    .then(res => res.json())
}


function isUserLoggedIn() {
    return fetch("/api/player")
    .then(response => response.json())
    .then(json => {
        if(json.status === 401) return "";
        else return json.player.email;
    })
}



function calculateLeaderBoardStats(gameData) {
    let gameArr = gameData.games;
    let scoreObj = {}
    for(let game of gameArr) {
        for(let gp of game.gamePlayers){

            let playerEmail = gp.player.email;
            let playerScore = gp.score;

            if(playerEmail in scoreObj) {
                scoreObj[playerEmail]["total"] += playerScore;
                if(playerScore === 1) scoreObj[playerEmail]["won"] += 1;
                if(playerScore === 0) scoreObj[playerEmail]["lost"] += 1;
                if(playerScore === 0.5) scoreObj[playerEmail]["tie"] += 1;
            }
            else {
                scoreObj[playerEmail] = {
                    total: 0,
                    won: 0,
                    lost: 0,
                    tie: 0
                };

                scoreObj[playerEmail]["total"] = playerScore;
                if(playerScore === 1) scoreObj[playerEmail]["won"] = 1;
                if(playerScore === 0) scoreObj[playerEmail]["lost"] = 1;
                if(playerScore === 0.5) scoreObj[playerEmail]["tie"] = 1;
            }
        }
    }
    return scoreObj;
}


function displayLeaderBoard(leaderBoardStats) {

    let headerTable = ["Name", "Total", "Won", "Lost", "Tie"];

    let body = document.getElementById("scoreboard")
    let table = document.createElement("table");

    // Create header
    let row = document.createElement("tr");
    row.style.fontWeight = "bold";
    for(let header of headerTable) {
       let column = document.createElement("td");
       column.style.padding = "15px";
       column.innerHTML = header;
       row.appendChild(column);
    }
    table.appendChild(row);

    // Create data rows
    for(let name in leaderBoardStats) {

        let total = leaderBoardStats[name]["total"];
        let won = leaderBoardStats[name]["won"];
        let lost = leaderBoardStats[name]["lost"];
        let tie = leaderBoardStats[name]["tie"];

        console.log(name, total, won, lost, tie);

        let row = document.createElement("tr");
        let columnName = document.createElement("td");
        columnName.innerHTML = name;
        let columnTotal = document.createElement("td");
        columnTotal.innerHTML = total;
        columnTotal.style.textAlign = "center";
        let columnWon = document.createElement("td");
        columnWon.innerHTML = won;
        columnWon.style.textAlign = "center";
        let columnLost = document.createElement("td");
        columnLost.innerHTML = lost;
        columnLost.style.textAlign = "center";
        let columnTie = document.createElement("td");
        columnTie.innerHTML = tie;
        columnTie.style.textAlign = "center";

        row.appendChild(columnName);
        row.appendChild(columnTotal);
        row.appendChild(columnWon);
        row.appendChild(columnLost);
        row.appendChild(columnTie);

        table.appendChild(row);
    }
    body.appendChild(table);
}


function displayGameList(gameObj) {

    let gameIdGet = document.getElementById("games");
    let olElem = document.createElement("ol");

    for(let game of gameObj.games) {
        let gameId = game.id;
        let date = formatDate(new Date(game.created));
        let numOfPlayers = game.gamePlayers.length;

        let liElem = document.createElement("li");
        liElem.className = game.gamePlayers.map(gp => "gp"+gp.id).join(" ");
        liElem.id = `gid-${gameId}`;

        console.log(liElem.className)

        liElem.innerHTML = `ROOM ${chance.animal()} (No. Player:${numOfPlayers} / ${date}) `
        // "date: " + date + "number of players: " + " (" + numOfPlayers + ")";

        olElem.appendChild(liElem);
        gameIdGet.appendChild(olElem);
    }
}

function formatDate(date) {
  var monthNames = [
    "January", "February", "March",
    "April", "May", "June", "July",
    "August", "September", "October",
    "November", "December"
  ];

  var day = date.getDate();
  var monthIndex = date.getMonth();
  var year = date.getFullYear();

  return day + ' ' + monthNames[monthIndex] + ' ' + year;
}


function leaderBoard() {

    // Fetch Game Data
    fetchGameData()

    // Calculate Statistics
    .then(gameData => {
        let scoreStats = calculateLeaderBoardStats(gameData);
        return scoreStats
    })

    // Display LeaderBoard
    .then(scoreStats => displayLeaderBoard(scoreStats))
}


function gameList() {

    // Fetch Game Data
    return fetchGameData()

    // Display
    .then(gameData => {
        displayGameList(gameData)
        gameLinks()
    })
}


function updateForm() {

    let loginForm = document.getElementById("login-form")
    let logoutForm = document.getElementById("logout-form")
    let alertSuccess = document.getElementById("login-alert-success")
    let alertFail = document.getElementById("login-alert-fail")
    let createGameButton = document.getElementById("createGame")

    isUserLoggedIn()
    .then(loggedIn => {

        if(!loggedIn) {
            loginForm.style.display = "block"
            logoutForm.style.display = "none"
            alertSuccess.style.display = "none"
            alertFail.style.display = "block"
            createGameButton.style.display = "none"
        } else {
            loginForm.style.display = "none"
            logoutForm.style.display = "block"
            alertSuccess.style.display = "block"
            alertSuccess.innerHTML = `Welcome you are logged-in ${loggedIn}`
            alertFail.style.display = "none"
            createGameButton.style.display = "block"
        }
    })
    .catch(error => {
        console.log(error)
        alertFail.style.display = "block"
    })
}


function intersection(arr1, arr2) {
    let newArr = [];
    for(let item of arr1) {
        if(arr2.includes(item)) newArr.push(item);
    }
    return newArr;
}


function gameLinks() {

    let listOfGames = document.querySelectorAll("li");

    isUserLoggedIn().then(username => {

        if(!username) return;


        fetch("/api/player/playerGameIds")
        .then(response => response.json())
        .then(function(json) {

            // select all the li elements
            let myGamePlayerIds = json.ids; // the games the current user belongs to

            for(let game of listOfGames) {

                let gameGPIdArr = game.className.split(' ').map(c => Number(c.replace('gp', '')));
                let findCommonId = intersection(myGamePlayerIds, gameGPIdArr);

                if(findCommonId.length === 0) {
                    // For other people's games
                    let joinGameLink = document.createElement("a");
                    joinGameLink.innerHTML = `Join Game`;
                    joinGameLink.href = `#`;
                    joinGameLink.addEventListener('click', function() {

                        // call api
                        let gameID = game.id.replace('gid-', '')

                        fetch(`/api/game/${gameID}/players`, {
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            method: "POST",
                            body: JSON.stringify({})
                        })
                        .then(res => res.json())
                        .then(json => {
                            window.location = `game.html?gp=${json.gpid}`;

                        })
                    })

                    game.appendChild(joinGameLink);
                }

                else {
                    // For your games
                    let goGameLink = document.createElement("a");
                    goGameLink.innerHTML = `Go Game`;
                    goGameLink.href = `game.html?gp=${findCommonId[0]}`;
                    game.appendChild(goGameLink);
                }
            }
        })
    })
}


function main() {

    // Create Leader Board
    leaderBoard()

    // Create Game List
    gameList()

    // Create Form
    updateForm()

}

/*
Login / Logout / Signup
*/
function login(email, password) {
    return $.post("/api/login", { username: email, password: password })
}

function logout() {
    return $.post("/api/logout")
}

function signup(email, password) {
    return fetch("/api/players", {
       headers: {
         'Accept': 'application/json',
         'Content-Type': 'application/json'
       },
       method: "POST",
       body: JSON.stringify({username: email, password: password})
     })
     .then(res => res.json)
}

function createNewGame() {
    return fetch("/api/games", {
       headers: {
         'Accept': 'application/json',
         'Content-Type': 'application/json'
       },
       method: "POST",
       body: JSON.stringify({})
   })
   .then(res => res.json())
}


document.getElementById("submit-btn-login").addEventListener("click", function(e){

    e.preventDefault();

    let email = document.getElementById("username-form").value
    let password = document.getElementById("password-form").value

    login(email, password)
    .done(res => {
        location.reload()
    })
    .catch(error => {})
})

document.getElementById("submit-btn-logout").addEventListener("click", function(e){

    e.preventDefault();

    logout()
    .done(res => {
        location.reload()
    })
    .catch(error => {})
})

document.getElementById("submit-btn-signup").addEventListener("click", function(e){

     e.preventDefault();

     let email = document.getElementById("username-form").value
     let password = document.getElementById("password-form").value

    signup(email, password)
    .then(function(res){
        login(email, password)
        .done(function() {
            location.reload()
        })
     })

})

document.getElementById("createGame").addEventListener("click", function(e){

    e.preventDefault();

    createNewGame()
    .then(function (json) {
       let gpid = json.gpid;
       window.location = `/web/game.html?gp=${gpid}`;
    })
    .catch(error => {

    })
})


main()


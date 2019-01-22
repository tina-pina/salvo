
// When creating game list, create two buttons already in the first place
// e.g. <li> ... <a>Go to Game</a> <a>Join Game</a>

// Later when user logged-in, select all the <li> under games
// then iterate li elements
// decide which button to show




function displayBoard() {

    let url = '/api/games';
    fetch(url).then(response => response.json()).then(function(gameObj) {
        console.log("this is games", gameObj);
        let leaderBoardStats = createLeaderBoardStats(gameObj);
        createTable(leaderBoardStats);
        createGamesList(gameObj);
        addLinkToGameList();
    })
}



function createGamesList(gameObj) {


    let gameIdGet = document.getElementById("games");
    let olElem = document.createElement("ol");

    for(let game of gameObj.games) {
        let gameId = game.id;
        let date = new Date(game.created * 1000);
        let numOfPlayers = game.gamePlayers.length;

        let liElem = document.createElement("li");
        liElem.className = game.gamePlayers.map(gp => "gp"+gp.id).join(" ");

        console.log(liElem.className)

        liElem.innerHTML = "date: " + date + "number of players: " + " (" + numOfPlayers + ")";

        olElem.appendChild(liElem);
        gameIdGet.appendChild(olElem);
    }
}


let createLeaderBoardStats = gameObj => {

    let gameArr = gameObj.games;

    let scoreObj = {}
    for(let game of gameArr) {
        for(let gp of game.gamePlayers){

            let playerEmail = gp.player.email;
            let playerScore = gp.player.score;

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

function createTable(leaderBoardStats) {

    let headerTable = ["Name", "Total", "Won", "Lost", "Tie"];

    let body =document.getElementById("scoreboard")
    let table = document.createElement("table");

    // Create header
    let row = document.createElement("tr");
    for(let header of headerTable) {
       let column = document.createElement("td");
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
        let columnWon = document.createElement("td");
        columnWon.innerHTML = won;
        let columnLost = document.createElement("td");
        columnLost.innerHTML = lost;
        let columnTie = document.createElement("td");
        columnTie.innerHTML = tie;

        row.appendChild(columnName);
        row.appendChild(columnTotal);
        row.appendChild(columnWon);
        row.appendChild(columnLost);
        row.appendChild(columnTie);

        table.appendChild(row);
    }

    body.appendChild(table);
}


function updateUserForm() {

    let loginForm = document.getElementById("login-form")
    let logoutForm = document.getElementById("logout-form")
    let alertSuccess = document.getElementById("login-alert-success")
    let alertFail = document.getElementById("login-alert-fail")
    let createGameButton = document.getElementById("createGame")

    fetch("/api/player")
    .then(response => response.json())
    .then(function(json) {
        console.log("this is players", json)
        if(json.status === 401) {
            console.log("user NOT logged-in")
            // hide logout form
            loginForm.style.visibility = "visible"
            logoutForm.style.visibility = "hidden"
            alertSuccess.style.visibility = "hidden"
            alertFail.style.visibility = "visible"
            createGameButton.style.visibility = "hidden"
        }
        else {

        //user will be directed to his own game
            console.log("user logged-in")


            //login styles
            // hide login form

            loginForm.style.visibility = "hidden"
            logoutForm.style.visibility = "visible"
            alertSuccess.style.visibility = "visible"
            alertSuccess.innerHTML = `Welcome you are logged-in ${json.player.name}`
            alertFail.style.visibility = "hidden"
            createGameButton.style.visibility = "visible"
        }
    })
    .catch(error => {
        console.log(error)
        alertFail.style.visibility = "visible"
    })
}

function addLinkToGameList() {

    fetch("/api/player/playerGameIds")
    .then(response => response.json())
    .then(function(json) {

        // select all the li elements
        let myGamePlayerIds = json.ids; // the games the current user belongs to
        let allLi = document.querySelectorAll('#games li')

        for(let liElem of allLi) {
            // if: check class name => if there is a gp id same as myGamePlayerIds => user is already in the game => Continue/Go to Game link
            // else: user is not in the game => Join Game link
        }
//
//        if(!gpIDs) return;
//
//        for(let id of gpIDs) {
//            let target = document.querySelector(`.gp${id}`);
//            let link = document.getElementById("linkId");
//            let aTag = document.createElement("a");
//            aTag.innerHTML = "Go to the game: "
//            aTag.className = "link";
//            aTag.id = "linkId"
//            aTag.href = `game.html?gp=${id}`
//            target.appendChild(aTag);
//        }

    })

}

document.getElementById("submit-btn-login").addEventListener("click", function(e){

    e.preventDefault();

    let email = document.getElementById("username-form").value
    let password = document.getElementById("password-form").value

    $.post("/api/login", { username: email, password: password })
    .done(function() {
        updateUserForm();
        addLinkToGameList();
    })
    .catch(error => {})
})

document.getElementById("submit-btn-logout").addEventListener("click", function(e){

    e.preventDefault();

    $.post("/api/logout")
    .done(function() {
        location.reload();
    })
    .catch(error => {})
})

document.getElementById("submit-btn-signup").addEventListener("click", function(e){

     e.preventDefault();

     let email = document.getElementById("username-form").value
     let password = document.getElementById("password-form").value

    fetch("/api/players",
    {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({username: email, password: password})
    })
    .then(function(res){
        $.post("/api/login", { username: email, password: password })
        .done(function() {
            updateUserForm();
        })
     })
    .catch(error => {

    })
})


displayBoard()
updateUserForm()


document.getElementById("createGame").addEventListener("click", function(e){

    e.preventDefault();

    fetch("/api/games",
    {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({})
    })
    .then(res => res.json())
    .then(function (json) {

       let gpid = json.gpid;
       window.location = `/game.html?gp=${gpid}`;

    })
    .catch(error => {

    })


})







//
//function paramObj(search) {
//  var obj = {};
//  var keyRes = " ";
//  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;
//
//  search.replace(reg, function(match, param, val) {
//    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
//  });
//
//  for(let key in obj) {
//    keyRes += obj.gp
//  }
//
//  return keyRes;
//}

//
//
//function displayBoard() {
//
////before login in we fetch data
//
//
//
//let gamePlayerIdWindow = paramObj(location.href);
//
//
//            let url = '/api/games';
//                fetch(url).then(response => response.json()).then(function(gameArr) {
//
//                    let gamePlayerIdArr = [];
//
//                    for( gameObj of gameArr) {
//                        let gamePlayersArr = gameObj.gamePlayers
//                        console.log(gamePlayersArr);
//                        for(let gamePlayerId of gamePlayersArr) {
//                            console.log(gamePlayerId.id);
//                            gamePlayerIdArr.push(gamePlayerId.id);
//                        }
//                    }
//                    console.log(gamePlayerIdArr);
//
//                    for(let singleId of gamePlayerIdArr) {
//                        if(singleId === gamePlayerIdWindow) {
//
//                            let url = (`/api/games/${gamePlayerId}`)
//
//                            displayBoard();
//
//                            fetch(url).then(response => response.json()).then(function(gameArr) {
//
//                                let leaderBoardStats = createLeaderBoardStats(gameArr);
//
//                                createTable(leaderBoardStats)
//
//                            });
//                        }
//                        else {
//
//                            let url = '/api/games';
//                                fetch(url).then(response => response.json()).then(function(gameArr) {
//
//                                    console.log("this is games", gameArr)
//
//                                    let leaderBoardStats = createLeaderBoardStats(gameArr);
//
//                                    createTable(leaderBoardStats)
//
//                             });
//
//                        }
//                    }
//            });
//
//
//
////i changed to here and i uncommented this down
//
//
//
//    let url = '/api/games';
//    fetch(url).then(response => response.json()).then(function(gameArr) {
//
//        console.log("this is games", gameArr)
//
//        let leaderBoardStats = createLeaderBoardStats(gameArr);
//
//        createTable(leaderBoardStats)
//
//    });
//
//
//    let createLeaderBoardStats = gameArr => {
//
//        let scoreObj = {}
//        for(let game of gameArr) {
//            for(let gp of game.gamePlayers){
//
//                let playerEmail = gp.player.email;
//                let playerScore = gp.player.score;
//
//                if(playerEmail in scoreObj) {
//                    scoreObj[playerEmail]["total"] += playerScore;
//                    if(playerScore === 1) scoreObj[playerEmail]["won"] += 1;
//                    if(playerScore === 0) scoreObj[playerEmail]["lost"] += 1;
//                    if(playerScore === 0.5) scoreObj[playerEmail]["tie"] += 1;
//                }
//                else {
//                    scoreObj[playerEmail] = {
//                        total: 0,
//                        won: 0,
//                        lost: 0,
//                        tie: 0
//                    };
//
//                    scoreObj[playerEmail]["total"] = playerScore;
//                    if(playerScore === 1) scoreObj[playerEmail]["won"] = 1;
//                    if(playerScore === 0) scoreObj[playerEmail]["lost"] = 1;
//                    if(playerScore === 0.5) scoreObj[playerEmail]["tie"] = 1;
//                }
//            }
//        }
//
//        return scoreObj;
//
//    }
//
//    function createTable(leaderBoardStats) {
//
//        let headerTable = ["Name", "Total", "Won", "Lost", "Tie"];
//
//        let body =document.getElementById("body")
//        let table = document.createElement("table");
//
//        // Create header
//        let row = document.createElement("tr");
//        for(let header of headerTable) {
//           let column = document.createElement("td");
//           column.innerHTML = header;
//           row.appendChild(column);
//        }
//        table.appendChild(row);
//
//        // Create data rows
//        for(let name in leaderBoardStats) {
//
//            let total = leaderBoardStats[name]["total"];
//            let won = leaderBoardStats[name]["won"];
//            let lost = leaderBoardStats[name]["lost"];
//            let tie = leaderBoardStats[name]["tie"];
//
//            console.log(name, total, won, lost, tie);
//
//            let row = document.createElement("tr");
//            let columnName = document.createElement("td");
//            columnName.innerHTML = name;
//            let columnTotal = document.createElement("td");
//            columnTotal.innerHTML = total;
//            let columnWon = document.createElement("td");
//            columnWon.innerHTML = won;
//            let columnLost = document.createElement("td");
//            columnLost.innerHTML = lost;
//            let columnTie = document.createElement("td");
//            columnTie.innerHTML = tie;
//
//            row.appendChild(columnName);
//            row.appendChild(columnTotal);
//            row.appendChild(columnWon);
//            row.appendChild(columnLost);
//            row.appendChild(columnTie);
//
//            table.appendChild(row);
//        }
//
//        body.appendChild(table);
//    }
//}
//
//function updateUserForm() {
//
//    let loginForm = document.getElementById("login-form")
//    let logoutForm = document.getElementById("logout-form")
//    let alertSuccess = document.getElementById("login-alert-success")
//    let alertFail = document.getElementById("login-alert-fail")
//
//    fetch("/api/player")
//    .then(response => response.json())
//    .then(function(json) {
//        console.log("this is players", json)
//        if(json.status === 401) {
//            console.log("user NOT logged-in")
//            // hide logout form
//            loginForm.style.visibility = "visible"
//            logoutForm.style.visibility = "hidden"
//            alertSuccess.style.visibility = "hidden"
//            alertFail.style.visibility = "visible"
//        }
//        else {
//
//        //user will be directed to his own game
//            console.log("user logged-in")

//            let gamePlayerIdWindow = paramObj(location.href);
//
//
//            let url = '/api/games';
//                fetch(url).then(response => response.json()).then(function(gameArr) {
//
//                    let gamePlayerIdArr = [];
//
//                    for(let gameObj of gameArr) {
//                        let gamePlayersArr = gameObj.gamePlayers
//                        console.log(gamePlayersArr);
//                        for(let gamePlayerId of gamePlayersArr) {
//                            console.log(gamePlayerId.id);
//                            gamePlayerIdArr.push(gamePlayerId.id);
//                        }
//                    }
//                    console.log(gamePlayerIdArr);
//
//                    for(let singleId of gamePlayerIdArr) {
//                        if(singleId === gamePlayerIdWindow) {
//
//                            let url = (`/api/games/${gamePlayerId}`)
//
//                            displayBoard();
//
//                            fetch(url).then(response => response.json()).then(function(gameArr) {
//
//                                let leaderBoardStats = createLeaderBoardStats(gameArr);
//
//                                createTable(leaderBoardStats)
//
//                            });
//                        }
//                    }
//            });


            //login styles
            // hide login form

//            loginForm.style.visibility = "hidden"
//            logoutForm.style.visibility = "visible"
//            alertSuccess.style.visibility = "visible"
//            alertSuccess.innerHTML = `Welcome you are logged-in ${json.player.name}`
//            alertFail.style.visibility = "hidden"
//        }
//    })
//    .catch(error => {
//        console.log(error)
//        alertFail.style.visibility = "visible"
//    })
//}
//
//document.getElementById("submit-btn-login").addEventListener("click", function(e){
//
//    e.preventDefault();
//
//    let email = document.getElementById("username-form").value
//    let password = document.getElementById("password-form").value
//
//    $.post("/api/login", { username: email, password: password })
//    .done(function() {
//        updateUserForm();
//    })
//    .catch(error => {})
//})
//
//document.getElementById("submit-btn-logout").addEventListener("click", function(e){
//
//    e.preventDefault();
//
//    $.post("/api/logout")
//    .done(function() {
//        updateUserForm();
//    })
//    .catch(error => {})
//})
//
//document.getElementById("submit-btn-signup").addEventListener("click", function(e){
//
//     e.preventDefault();
//
//     let email = document.getElementById("username-form").value
//     let password = document.getElementById("password-form").value
//
//    fetch("/api/players",
//    {
//        headers: {
//          'Accept': 'application/json',
//          'Content-Type': 'application/json'
//        },
//        method: "POST",
//        body: JSON.stringify({username: email, password: password})
//    })
//    .then(function(res){
//        $.post("/api/login", { username: email, password: password })
//        .done(function() {
//            updateUserForm();
//        })
//     })
//    .catch(error => {
//
//    })
//})
//
//
//
//
//displayBoard()
//updateUserForm()




//// ### GOAL ###
//// create an object
//// { Email: {Total: totalVal, Won: Val, Lost: Val, Tied: Val} }
//// {
////     j.bauer@ctu.gov:	{total: 2.0, won: 1, lost: 0, tied: 2}
////     c.obrian@ctu.gov:	{ ... },
////     t.almeida@ctu.gov:	{ ... },
//// }
//
//// Is user logged-in?
//
//let updateForm = () => {
//    fetch("/api/player")
//    .then(response => response.json())
//    .then(function(json) {
//        console.log(json)
//        if(json.status === 401) {
//            console.log("user NOT logged-in")
//            // hide logout form
//            let logoutForm = document.getElementById("logout-form")
//            logoutForm.style.visibility = "hidden"
//        }
//        else {
//            console.log("user logged-in")
//            // hide login form
//            let loginForm = document.getElementById("login-form")
//            loginForm.style.visibility = "hidden"
//        }
//    })
//    .catch(error => {
//        console.log(error)
//    })
//}
//
//updateForm();
//
//
// Show leader board

//
//
////function to the login button to post the data to your login URL, using AJAX.
//
//document.getElementById("submit-btn-login").addEventListener("click", function(e){
//
//    e.preventDefault();
//
//    let email = document.getElementsByTagName("input").item(0).value;
//    let password = document.getElementsByTagName("input").item(1).value;
//
//    $.post("/api/login", { username: email, password: password })
//    .done(function() {
//
//        // call player api - check if you can get info
//        let url = '/api/player';
//        fetch(url)
//        .then(response => response.json())
//        .then(function(playerInfo) {
//
//            let alert = document.getElementById("login-alert-success")
//            alert.innerHTML = `Login successful! Welcome ${playerInfo.player.name}!`
//            alert.className = alert.className.replace("invisible", "")
//
//        })
//        .catch(error => {
//            let alert = document.getElementById("login-alert-fail")
//            alert.className = alert.className.replace("invisible", "")
//        })
//    })
//    .done(() => {
//        // refresh login
//        updateForm();
//    })
//    .catch(error => {
//        let alert = document.getElementById("login-alert-fail")
//        alert.className = alert.className.replace("invisible", "")
//    })
//
//
////  return false;
//})
//
//
////logout button
//
//document.getElementById("submit-btn-logout").addEventListener("click", function(e){
//
//    e.preventDefault();
//
//    let email = document.getElementsByTagName("input").item(0).value;
//    let password = document.getElementsByTagName("input").item(1).value;
//
//    $.post("/api/logout").done(function() {})
//    .catch(error => {
//        console.log("something went wrong!")
//    })
//
//})
//
//
//

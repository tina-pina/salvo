// ### GOAL ###
// create an object
// { Email: {Total: totalVal, Won: Val, Lost: Val, Tied: Val} }
// {
//     j.bauer@ctu.gov:	{total: 2.0, won: 1, lost: 0, tied: 2}
//     c.obrian@ctu.gov:	{ ... },
//     t.almeida@ctu.gov:	{ ... },
// }



let url = '/api/games';
fetch(url).then(response => response.json()).then(function(gameArr) {

    let leaderBoardStats = createLeaderBoardStats(gameArr);

    createTable(leaderBoardStats)

});

let createLeaderBoardStats = gameArr => {

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

    let body =document.getElementById("body")
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




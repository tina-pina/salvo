


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

//console.log(paramObj(location.href));

let gpPlayerId = paramObj(location.href);


//let url = '/web/game.html?gp=${gpPlayerId}';

let url =  '/web/game.html?gp='+gpPlayerId;
header = {'Content-Type': 'application/json',
          'Accept': 'application/json'}


fetch(url, {headers: header})
//.then(response => response.json())
.then(res => res.text())
.then(text => console.log(text))






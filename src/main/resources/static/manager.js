$(function() {

  // display text in the output area
  function showOutput(text) {
    $("#output").text(text);
  }

  // load and display JSON sent by server for /players

  function loadData() {
    $.get("/players")
    .done(function(data) {
      showOutput(JSON.stringify(data, null, 2));
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }

  // handler for when user clicks add person

  function addPlayer() {
    var name = $("#email").val();
    if (name) {
      postPlayer(name);
    }
  }

  // code to post a new player using AJAX
  // on success, reload and display the updated data from the server

  function postPlayer(userName) {

    let firstName = userName.split(" ")[0]
    let lastName = userName.split(" ")[1]
    let url = "/players"
    fetch(url, {
      method: 'POST', // or 'PUT'
      body: JSON.stringify({ "firstName": firstName, "lastName": lastName }), // data can be `string` or {object}!
      headers:{
        'Content-Type': 'application/json'
      }
    }).then(res => res.json())
    .then(response => console.log('Success:', JSON.stringify(response)))
    .then(() => loadData())
    .catch(error => console.error('Error:', error));
//    $.post({
//      headers: {
//          'Content-Type': 'application/json'
//      },
//      dataType: "text",
//      url: "/players",
//      data: JSON.stringify({ "firstName": firstName, "lastName": lastName })
//    })
//    .done(function( ) {
//      showOutput( "Saved -- reloading");
//      loadData();
//    })
//    .fail(function( jqXHR, textStatus ) {
//      showOutput( "Failed: " + textStatus );
//    });
  }

  $("#add_player").on("click", addPlayer);

  loadData();
});
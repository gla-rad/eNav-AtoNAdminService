/**
 * Global variables
 */
var stompClient = null;
var noOfMessages = 0;
var maxNoOfMessages = 100;

/**
 * This function initialises the served page when the web-socket client gets
 * connected.
 */
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#incoming").show();
    }
    else {
        $("#incoming").hide();
    }
    $("#incoming").html("");
}

/**
 * This function handles the connection to the web-socket served by our
 * web-app. Basically, it connects and starts printing out the messages.
 */
function connect() {
    var endpoint = $( "#endpoint option:selected" ).text();
    if(stompClient == null) {
        var socket = new SockJS(window.location.pathname + 'aton-service-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, (frame) => {
            setConnected(true);
            stompClient.subscribe('/topic/' + endpoint, (msg) => {
                showMessage(JSON.parse(msg.body));
            });
        });
    } else {
        setConnected(true);
        stompClient.subscribe('/topic/' + endpoint, (msg) => {
            showMessage(JSON.parse(msg.body));
        });
    }
}

/**
 * This function handles the disconnections from the web-socket served by our
 * web-app. Basically, it unsubscribes and clears out all the messages.
 */
function disconnect() {
    if (stompClient !== null) {
        // Try to remove all the previous subscriptions
        for (const sub in stompClient.subscriptions) {
            if (this.stompClient.subscriptions.hasOwnProperty(sub)) {
                this.stompClient.unsubscribe(sub);
            }
        }
    }
    setConnected(false);
    console.log("Disconnected");
}

/**
 * Simple helper function that appends the incoming messages onto a table.
 * If more messages than the maximum number are receives, it will clear out
 * the table and start over.
 */
function showMessage(msg) {
    // For too many messages clear out the incoming table
    if(noOfMessages >= maxNoOfMessages) {
        $("#incoming").html("");
        noOfMessages = 0;
    }

    // And add the entry to the table
    $("#incoming").append("<tr class=\"d-flex\">"
        + "<td class=\"col-4\">" + msg.idCode + "</td>"
        + "<td class=\"col-4\">" + msg.textualDescription + "</td>"
        + "<td class=\"col-2\">" + msg.dateStart + "</td>"
        + "<td class=\"col-2\">" + msg.dateEnd + "</td>"
        + "</tr>");

    // Increase the number of shown messages
    noOfMessages++;
}

/**
 * Standard jQuery initialisation of the page were all buttons are assigned an
 * operation and the form doesn't really do anything.
 */
$(() => {
    $( "#connect" ).click(() => { connect(); });
    $( "#disconnect" ).click(() => { disconnect(); });
    $("form").on('submit', (e) => {
        e.preventDefault();
    });
});
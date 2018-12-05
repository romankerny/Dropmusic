var websocket = null;
var email = "";


window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
    connect('ws://' + window.location.host +'/WebSocket/ws/' + email);
}


function setEmail(email) {
    this.email = email;
}

function connect(host) { // connect to the host websocket
    if ('WebSocket' in window)
        websocket = new WebSocket(host);
    else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
    else {
        writeNotification('Get a real browser which supports WebSocket.');
        return;
    }

    // websocket.onopen    = onOpen; // set the 4 event listeners below
    websocket.onclose   = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror   = onError;
}


function onClose(event) {
    document.getElementById('chat').onkeydown = null;
}

function onMessage(message) { // print the received message
    writeNotification(message.data);
}

function onError(event) {
    writeNotification('WebSocket error.');
    document.getElementById('chat').onkeydown = null;
}

function writeNotification(text) {
    var history = document.getElementById('notifications');
    var line = document.createElement('p');
    line.style.wordWrap = 'break-word';
    line.innerHTML = text;
    history.appendChild(line);
    history.scrollTop = history.scrollHeight;
}


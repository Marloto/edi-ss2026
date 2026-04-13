const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8081 });

const clients = [];

function onConnectionCreated(clientConnection, req) {
    const path = req.url; // z.B. /some/path/to/endpoint

    clients.push(clientConnection);
    
    // Wenn eine nachricht am Server eingeht
    clientConnection.on('message', (message) => {
        console.log("Recieved: " + message);
        clients.forEach(client => client.send(message));
    });

    // Wenn die Verbindung geschlossen wird
    clientConnection.on("close", () => {
        console.log("Closed...");
    });

    // clientConnection.send('something');
}

// Ereignis "connection" => dann folgende Methode ausführen
console.log("Start...");
wss.on('connection', onConnectionCreated);
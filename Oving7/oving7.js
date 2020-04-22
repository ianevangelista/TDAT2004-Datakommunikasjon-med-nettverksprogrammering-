"use strict";

const net = require("net");
const crypto = require("crypto");

// Simple HTTP server responds with a simple WebSocket client test
const httpServer = net.createServer(connection => {
  connection.on("data", () => {
    let content = `<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
  </head>
  <body>
    WebSocket test page
    <button onClick="sendMsg()">Send melding</Button>
    <div id="messages"></div>
    <script>
      let ws = new WebSocket('ws://localhost:3001');
      ws.onmessage = event => {document.getElementById("messages").appendChild(document.createTextNode(event.data))};
      ws.onopen = () => ws.send('hello');
      function sendMsg() {
        ws.send("This is a message, hello studass.");
      }
    </script>
  </body>
</html>
`;
    connection.write(
      "HTTP/1.1 200 OK\r\nContent-Length: " + content.length + "\n\n" + content
    );
  });
});
httpServer.listen(3000, () => {
  console.log("HTTP server listening on port 3000");
});

// Incomplete WebSocket server
var connections = new Set();
const wsServer = net.createServer(connection => {
  console.log("Client connected");
  var state = 0;
  connections.add(connection);

  connection.on("data", data => {
    if (state === 0) {
      console.log("Headers received from client:\n" + data.toString());
      var headers = data.toString().split("\r\n");
      var key = headers
        .find(header => header.indexOf("Sec-WebSocket-Key") !== -1)
        .split(": ")[1];
      var acceptValue = generateAcceptValue(key);
      const responseHeaders = [
        "HTTP/1.1 101 Switching Protocols",
        "Upgrade: websocket",
        "Connection: Upgrade",
        `Sec-WebSocket-Accept: ${acceptValue}`
      ];
      connection.write(responseHeaders.join("\r\n") + "\r\n\r\n");
      state = 1;
    } else {
      console.log("Data recieved from client: ", data);
      let bytes = Buffer.from(data);
      let length = bytes[1] & 127;
      let maskStart = 2;
      if (length == 126) {
        maskStart = 4;
      } else if (length == 127) {
        maskStart = 10;
      }
      let dataStart = maskStart + 4;
      let output = "";
      for (let i = dataStart; i < dataStart + length; i++) {
        let byte = bytes[i] ^ bytes[maskStart + ((i - dataStart) % 4)];
        output += String.fromCharCode(byte);
      }
      for (let sock of connections) {
        send(output, sock);
      }
    }
  });

  connection.on("message", message => {
    console.log("Message received from client: ", message);
  });

  connection.on("ready", message => {
    console.log("DETTE ER EN CONNECTION");
  });

  connection.on("error", error => {
    console.error("Error: ", error);
  });

  connection.on("end", () => {
    console.log("Client disconnected");
    connections.delete(connection);
  });
});

const generateAcceptValue = acceptKey => {
  return crypto
    .createHash("sha1")
    .update(acceptKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", "binary")
    .digest("base64");
};

function send(msg, socket) {
  // http://stackoverflow.com/questions/8214910/node-js-websocket-send-custom-data
  var newFrame = new Buffer(msg.length > 125 ? 4 : 2);
  newFrame[0] = 0x81;
  if (msg.length > 125) {
    newFrame[1] = 126;
    var length = msg.length;
    newFrame[2] = length >> 8;
    newFrame[3] = length & 0xff;
  } else {
    newFrame[1] = msg.length;
  }
  socket.write(newFrame, "binary");
  socket.write(msg, "utf8");
}

wsServer.listen(3001, () => {
  console.log("WebSocket server listening on port 3001");
});

const express = require("express");
const http = require("http");
const socketio = require("socket.io");

const app = express();
const server = http.Server(app);
const io = socketio(server);

server.listen(8080, () => {
  console.log("Listening on 8080");
});

io.on("connection", (socket) => {
  console.log("Player connected.");
  // socket.emit("socketID", { id: socket.id });
  socket.on("newRoom", (args) => {
    let roomName = args["roomName"];
    console.log(`Joined room ${roomName}`);
    socket.join(roomName);
  });
  socket.on("updateFromPlayer", (args) => {
    console.log(args);
    socket.to(args["roomName"]).emit("updateFromServer", args);
  });
  socket.on("disconnect", () => {
    console.log("Player disconnected.");
  });
});

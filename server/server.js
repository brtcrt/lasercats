/**
 * Room Object Type Definiton
 * @typedef {Object} Room
 * @property {String} roomId - Id of room
 * @property {String} roomName - Name of room
 * @property {[String]} players - List of player ids
 */

const express = require("express");
const http = require("http");
const socketio = require("socket.io");
const uuid = require("uuid");
const findRoom = require("./utils/findRoom");
const findByPlayerId = require("./utils/findByPlayerId");

const app = express();
const server = http.Server(app);
const io = socketio(server);

let rooms = [
  {
    roomId: uuid.v4(),
    roomName: "a",
    players: [22222],
  },
  {
    roomId: uuid.v4(),
    roomName: "b",
    players: [312323],
  },
];

server.listen(8080, () => {
  console.log("Listening on 8080");
});

/*
List of Events

Listeners
  -connection
  -getRooms
  -newRoomReq
  -joinRoomReq
  -updateFromPlayer
  -disconnect  

Emitters
  -updateRooms
  -newRoomRes
  -joinRoomRes
  -updateFromServer
*/

io.on("connection", (socket) => {
  console.log("Player connected.");
  socket.on("getRooms", (args) => {
    socket.emit("updateRooms", { rooms: rooms });
  });
  socket.on("newRoomReq", (args) => {
    const index = findRoom(rooms, args);
    const roomName = args["roomName"];
    if (index != -1) {
      // already exists
      console.log(`Room "${roomName}" already exists`);
      socket.emit("createRoomRes", {
        code: 406, // http response code for "Not Acceptable"
        message: "A room with that name already exists.",
        payload: {},
      });
    } else {
      const room = {
        roomId: uuid.v4(),
        roomName: roomName,
        players: [socket.id],
      };
      console.log(`Created room ${roomName}: ${JSON.stringify(room)}`);
      rooms.push(room);
      socket.emit("createRoomRes", {
        code: 201, // https response code for "Created"
        message: `Created a room with name ${room.name}`,
        payload: room,
      });
      socket.join(room.roomId);
    }
  });
  socket.on("joinRoomReq", (args) => {
    const index = findRoom(rooms, args);
    // console.log(`Request to join room: ${JSON.stringify(args)}`);
    if (index == -1) {
      // doesn't exist
      socket.emit("joinRoomRes", {
        code: 404, // http response code for "Not Found"
        message: "No such room exists.",
        payload: {},
      });
    } else {
      // exists
      const r = rooms[index];

      if (r.players.length < 2) {
        // and has empty space
        // additionally if already in a room
        if (Object.keys(args["currentRoom"]).length > 0) {
          const old_room = findRoom(rooms, args["currentRoom"]);
          if (rooms[old_room].players.length < 2) {
            // if old room only had current player, delete it
            rooms.splice(old_room, 1);
          } else {
            // if not then remove the player from room["players"]
            const player_index = rooms[old_room].players.indexOf(socket.id);
            if (player_index > -1) {
              // double check to see if player is in the room
              rooms[old_room].players.splice(player_index, 1);
            }
          }
        }
        rooms[index].players.push(socket.id);
        socket.emit("joinRoomRes", {
          code: 202, // http response code for "Accepted"
          message: `Joined Room ${rooms[index].roomName}`,
          payload: rooms[index],
        });
        socket.join(r.roomId);
      } else {
        // or is full
        socket.emit("joinRoomRes", {
          code: 406, // http response code for "Not Acceptable"
          message: `No space in room ${rooms[index].roomName}`,
          payload: {},
        });
      }
    }
  });
  socket.on("updateFromPlayer", (args) => {
    // console.log(args);
    // console.log(rooms);
    socket.to(args["roomId"]).emit("updateFromServer", args);
  });
  socket.on("disconnect", () => {
    // leave room on disconnect
    const index = findByPlayerId(rooms, socket.id);
    if (index > -1) {
      if (rooms[index].players.length < 2) {
        rooms.splice(index, 1);
      } else {
        rooms[index].players.splice(rooms[index].players.indexOf(socket.id), 1);
      }
    }
    console.log("Player disconnected.");
  });
});

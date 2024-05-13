/**
 * Room Object Type Definiton
 * @typedef {Object} Room
 * @property {String} roomId - Id of room
 * @property {String} roomName - Name of room
 * @property {[String]} players - List of player ids
 * @property {String} passwordHash - Hash of the password
 */

const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const uuid = require("uuid");
const findRoom = require("./utils/findRoom");
const findByPlayerId = require("./utils/findByPlayerId");

const io = new Server(8080, {
  maxHttpBufferSize: 1e8,
  pingTimeout: 600_000,
  connectionStateRecovery: {
    // the backup duration of the sessions and the packets
    maxDisconnectionDuration: 2 * 60 * 1000,
    // whether to skip middlewares upon successful recovery
    skipMiddlewares: true,
  },
});

let clients = [
  {
    socketID: 22222,
    clientID: "acasda-123asd-12asd-df3412",
  },
  {
    socketID: 312323,
    clientID: "asdasx-xaswd-sdfgsd-df3412",
  },
];

let rooms = [
  {
    roomId: uuid.v4(),
    roomName: "a",
    players: [22222],
    passwordHash: "",
  },
  {
    roomId: uuid.v4(),
    roomName: "b",
    players: [312323],
    passwordHash:
      "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", // hash of abc
  },
];

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
  socket.on("newPlayer", (args) => {
    for (let i = 0; i < clients.length; i++) {
      if (clients[i].clientID == args["clientID"]) {
        clients[i].socketID = socket.id;
        return;
      }
    }
    clients.push({"socketID": socket.id, "clientID": args["clientID"]});
    console.log(args);
    console.log(clients);
  });
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
      // If already in a room. I wasn't checking for this before for some reason idk
      if (args["currentRoom"]) {
        const old_room = findRoom(rooms, args["currentRoom"]);
        if (rooms[old_room].players.length < 2) {
          // if old room only had current player, delete it
          rooms.splice(old_room, 1);
        } else {
          // if not then remove the player from room["players"]
          const player_index = rooms[old_room].players.indexOf(args["clientID"]);
          if (player_index > -1) {
            // double check to see if player is in the room
            rooms[old_room].players.splice(player_index, 1);
          }
        }
      }
      const room = {
        roomId: uuid.v4(),
        roomName: roomName,
        players: [args["clientID"]],
        passwordHash: args["passwordHash"],
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
      // check for password
      if (args.passwordHash != r.passwordHash) {
        socket.emit("joinRoomRes", {
          code: 401, // http response code for "Unauthorized"
          message: `Wrong password for room ${rooms[index].roomName}`,
          payload: {},
        });
        return;
      }

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
            const player_index = rooms[old_room].players.indexOf(args["clientID"]);
            if (player_index > -1) {
              // double check to see if player is in the room
              rooms[old_room].players.splice(player_index, 1);
            }
          }
        }
        rooms[index].players.push(args["clientID"]);
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
  socket.on("disconnect", (reason) => {
    // leave room on disconnect
    let clientID = "";
    for (let i = 0; i < clients.length; i++) {
      if (clients[i].socketID == socket.id) {
        clientID = clients[i].clientID;
        return;
      }
    }
    const index = findByPlayerId(rooms, clientID);
    if (index > -1) {
      if (rooms[index].players.length < 2) {
        rooms.splice(index, 1);
      } else {
        rooms[index].players.splice(rooms[index].players.indexOf(clientID), 1);
      }
    }
    console.log("Player disconnected. Reason: " + reason);
  });
  socket.on("closeClient", (args) => {
    const index = findByPlayerId(rooms, args["clientID"]);
    if (index > -1) {
      if (rooms[index].players.length < 2) {
        rooms.splice(index, 1);
      } else {
        rooms[index].players.splice(rooms[index].players.indexOf(args["clientID"]), 1);
      }
    }
  })
});

/**
 *
 * @param {[Room]} rooms
 * @param {{}} args
 * @returns
 */
const findRoom = (rooms, args) => {
  let index = -1;
  if (args["roomName"] != null) {
    // searching via room name
    let roomName = args["roomName"];
    for (let i = 0; i < rooms.length; i++) {
      const r = rooms[i];
      if (r.roomName == roomName) {
        index = i;
      }
    }
    return index;
  }

  if (args["roomId"] != null) {
    // searching via room id
    let roomId = args["roomId"];
    for (let i = 0; i < rooms.length; i++) {
      const r = rooms[i];
      if (r.roomId == roomId) {
        index = i;
      }
    }
    return index;
  }
  // invalid args
  return index;
};

module.exports = findRoom;

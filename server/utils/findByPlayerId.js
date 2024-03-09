/**
 *
 * @param {[Room]} rooms
 * @param {String} id
 * @returns
 */
const findByPlayerId = (rooms, id) => {
  let index = -1;
  for (let i = 0; i < rooms.length; i++) {
    if (rooms[i].players.indexOf(id) > -1) {
      index = i;
    }
  }
  // invalid args
  return index;
};

module.exports = findByPlayerId;

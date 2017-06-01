const mongoose        = require('./config'),
      jugadorSchema   = require('./schemas').jugadorSchema

const models = {
  Jugador   : mongoose.model('jugador', jugadorSchema)
};

module.exports = models;

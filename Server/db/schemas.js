const mongoose  = require('./config'),
      Schema    = mongoose.Schema;

      const schemas = {

        jugadorSchema : new Schema({
          nombre      : String,
          tiempo      : Number
        })

      };

module.exports = schemas;

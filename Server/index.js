const express     = require('express'),
      bodyParser  = require('body-parser'),
      mongoose    = require('mongoose'),
      methodOverride  = require('method-override')

const app = express()

const JugadorController = require('./jugador')

app.use(bodyParser.urlencoded({extended:false}))
app.use(bodyParser.json())
app.use(methodOverride())

const api = express.Router()

api.route('/jugadores')
  .get(JugadorController.findAll)
  .post(JugadorController.add)

app.use('/api',api)

app.listen(3500, ()=>{
  console.log('Funcionando en puerto 3500');
});

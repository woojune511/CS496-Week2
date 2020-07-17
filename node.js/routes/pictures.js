const router = require('express').Router();
const Todo = require('../models/picture');

// Find All
router.get('/', (req, res) => {
  Todo.findAll()
    .then((pictures) => {
      if (!pictures.length) return res.status(404).send({ err: 'Picture not found' });
      res.send(`find successfully: ${pictures}`);
    })
    .catch(err => res.status(500).send(err));
});

// Find One by name
router.get('/pictures/:name', (req, res) => {
  Todo.findOneByTodoid(req.params.todoid)
    .then((todo) => {
      if (!todo) return res.status(404).send({ err: 'Todo not found' });
      res.send(`findOne successfully: ${todo}`);
    })
    .catch(err => res.status(500).send(err));
});

// Create new todo document
router.post('/', (req, res) => {
  Todo.create(req.body)
    .then(todo => res.send(todo))
    .catch(err => res.status(500).send(err));
});

// Update by todoid
router.put('/pictures/:name', (req, res) => {
  Todo.updateByTodoid(req.params.name, req.body)
    .then(picture => res.send(picture))
    .catch(err => res.status(500).send(err));
});

// Delete by todoid
router.delete('/pictures/:name', (req, res) => {
  Todo.deleteByTodoid(req.params.name)
    .then(() => res.sendStatus(200))
    .catch(err => res.status(500).send(err));
});

module.exports = router;
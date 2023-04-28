const express = require('express');
const mongoose = require('mongoose');

// Create the express app
const app = express();

// Set up middleware for parsing request bodies
app.use(express.urlencoded({ extended: true }));

// Set up the database connection
mongoose.connect('mongodb://0.0.0.0:27017/NodeAndMongo2_db', { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => {
    console.log('Connected to database');
  })
  .catch(err => {
    console.error('Error connecting to database:', err);
    process.exit(1);
  });

// Define the Subscriber schema and model
const subscriberSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true },
  zipCode: { type: Number, required: true },
});
const Subscriber = mongoose.model('Subscriber', subscriberSchema);

// Define the routes

// Home page
app.get('/', (req, res) => {
  res.send('<h1>NodeAndMongo2 Web-Application</h1><a href="/contact"><button>Add Subscriber</button></a>');
});

// Add subscriber page
app.get('/contact', (req, res) => {
  res.send(`
    <h1>Add Subscriber</h1>
    <form method="POST" action="/subscribe">
      <div>
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" required>
      </div>
      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
      </div>
      <div>
        <label for="zipCode">Zip Code:</label>
        <input type="number" id="zipCode" name="zipCode" required>
      </div>
      <div>
        <button type="submit" name="submit">Submit</button>
      </div>
    </form>
  `);
});

// Handle new subscriber form submission
app.post('/subscribe', (req, res) => {
  // Create a new Subscriber document
  const subscriber = new Subscriber({
    name: req.body.name,
    email: req.body.email,
    zipCode: req.body.zipCode,
  });

  // Save the new Subscriber document to the database
  subscriber.save()
    .then(() => {
      res.redirect('/subscribers');
    })
    .catch(err => {
      console.error(err);
      res.status(500).send('Error saving subscriber');
    });
});

// List all subscribers
app.get('/subscribers', (req, res) => {
  Subscriber.find()
    .then(subscribers => {
      res.send(`
        <h1>Subscribers</h1>
        <ul>
          ${subscribers.map(subscriber => `<li>${subscriber.name} - ${subscriber.email} - ${subscriber.zipCode}</li>`).join('')}
        </ul>
      `);
    })
    .catch(err => {
      console.error(err);
      res.status(500).send('Error fetching subscribers');
    });
});

// Handle 404 errors
app.use((req, res) => {
  res.status(404).send(`
    <h1>404 Not Found</h1>
    <p>The requested URL ${req.url} was not found on this server.</p>
  `);
});

// Start the server
const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Listening on port ${port}`);
});


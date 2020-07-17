var Client = require('mongodb').MongoClient;

Client.connect('mongodb://localhost:27017/test', function(error, db){
    if(error) {
        console.log(error);
    } else {
        console.log("connected:"+db);
        var adminDb = db.db('test').admin();
        // List all the available databases
        adminDb.listDatabases(function(err, result) {
          console.log(result.databases);
        })
        db.close();
    }
});
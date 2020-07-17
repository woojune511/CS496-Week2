var mongodb = require('mongodb');
var ObjectID = mongodb.ObjectID;
var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');


var genRandomString = (length)=>{
    return crypto.randomBytes(Math.ceil(length/2))
    .toString('hex')
    .slice(0, length);
};

var sha512 = (password, salt) => {
    var hash = crypto.createHmac('sha512', salt);
    hash.update(password);
    var value = hash.digest('hex');
    return {
        salt: salt,
        passwordHash:value
    };
};

function saltHashPassword(userPassword) {
    var salt = genRandomString(16);
    var passwordData= sha512(userPassword, salt);
    return passwordData;
}

function checkHashPassword(userPassword, salt){
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

var MongoClient = mongodb.MongoClient;

var url = 'mongodb://localhost:27017'

MongoClient.connect(url, {useNewUrlParser: true}, (err, client)=>{
    if(err)
        console.log('Unable to connect to the mongoDB server.Error', err);
    else{
        app.listen(3000, ()=>{
            console.log('Connected to mongoDB server, WebService running on port 3000');
        });
    }
})
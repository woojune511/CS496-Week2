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

var url = 'mongodb://localhost:27017';

MongoClient.connect(url, {useNewUrlParser: true}, (err, client)=>{
    if(err)
        console.log('Unable to connect to the mongoDB server.Error', err);
    else{

        app.post('/register', (request, response, next)=>{
            var post_data = request.body;

            var plaint_passowrd = post_data.password;
            var hash_data = saltHashPassword(plaint_passowrd);

            var password = hash_data.passwordHash;
            var salt = hash_data.salt;

            var name = post_data.name;
            var email = post_data.email;

            var insertJson = {
                'email': email,
                'password': password,
                'salt': salt,
                'name': name
            };
            var db = client.db('test');

            db.collection('user')
                .find({'email': email}).count((err, num)=>{
                    if(num !=0){
                        response.json('Email already exists');
                        console.log('Email already exists');
                    }
                    else{
                        db.collection('user')
                            .insertOne(insertJson, (error, res)=>{
                                response.json('Registration succeeded');
                                console.log('Registration succeeded');
                            });
                    }
                })
        })

        app.post('/login', (request, response, next)=>{
            var post_data = request.body;
            
            var email = post_data.email;
            var userPassword = post_data.password;

            var db = client.db('test');

            db.collection('user')
                .find({'email': email}).count((err, num)=>{
                    if(num ==0){
                        response.json('Email does not exist');
                        console.log('Email does not exist');
                    }
                    else{
                        db.collection('user')
                            .findOne({'email': email}, (error, user)=>{
                                var salt = user.salt;
                                var hashed_password = checkHashPassword(userPassword, salt).passwordHash;
                                var encrypted_password = user.password;
                                if(hashed_password==encrypted_password){
                                    response.json('Login succeeded');
                                    console.log('Login succeeded');
                                }
                                else{
                                    response.json('Wrong password');
                                    console.log('Wrong password');
                                }
                            });
                    }
                })
        });

        app.listen(3000, ()=>{
            console.log('Connected to mongoDB server, WebService running on port 3000');
        });
    }
});
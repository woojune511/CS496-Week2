var http = require("http");
var u = require("url");
var qs = require("querystring");

var mongo =require("mongodb").MongoClient;

var fs = require("fs");

var server = http.createServer(function(request, response){
    var url = request.url;
    var structedUrl = u.parse(url);
    var path = structedUrl.pathname;
    var cmds = path.split("/");

    if(cmds[1] == "html"){
        console.log("OK");
        var filePath = path.substring(1);
        console.log(path);
        fs.readFile(filePath, 'utf-8', (error, data)=>{
            if(error){
                response.writeHead(404, {'Content-Type': 'text/html'});
                response.end("<h1>404 Page not found!</h1>");
            }

            else{
                response.writeHead(200, {'Content-Type': 'text/html'});
                response.end(data);
            }
        });
    }
    
    else if(cmds[1] == "html"){
        if(request.method == "GET"){
            mongo.connect("mongodb://localhost:27017/serverdb", (error, db)=>{
                if(error){
                    response.write(error);
                    response.end("");
                }

                else{
                    var datas = qs.parse(structedUrl.query);
                    var cursor = db.collection("user").find(datas);
                    var item ={
                        id: "",
                        pw: ""
                    };
                    cursor.toArray((err, dataset)=>{
                        console.log(dataset.length);
                        for(var i=0; i<dataset.length; i+=1){
                            item = dataset[i];
                            console.log(item.id);
                            console.log(item.pw);
                        }
                    });
                    response.end("GET OK");
                }
            });
        }

        else if(request.method == "POST"){
            var postdata = "";
            request.on("data", (fragment)=>{
                postdata += fragment;
            });

            request.on("end", ()=>{
                mongo.connect("mongodb://localhost:27017/serverdb", (error, db)=>{
                    if(error){
                        response.end(error);
                    }
                    else{
                        var datas = qs.parse(postdata);
                        var cursor = db.collection("user").find(datas);
                        cursor.toArray((err, dataset)=>{
                            for (var i=0; i<dataset.length; i+=1){
                                item = dataset[i];
                                console.log(item.id);
                                console.log(item.pw);
                            }
                        });
                        response.end("POST OK");
                    }
                });
            });
        }
    }
});

server.listen(9000, ()=>{
    console.log("Server listening....");
});
// routes/index.js

module.exports = function(app, Picture)
{
    // GET ALL PICTURES
    app.get('/api/pictures', function(req,res){
        Picture.find(function(err, pictures){
            if(err) return res.status(500).send({error: 'database failure'});
            res.json(pictures);
        })
    });

    // GET SINGLE PICTURE
    app.get('/api/pictures/:name', function(req, res){
        Picture.findOne({_id: req.params.name}, (err, picture) => {
            if(err) return res.status(500).json({error: err});
            if(!picture) return res.status(404).json({error: 'picture not found'});
            res.json(picture);
        })
    });

    // GET PICTURE BY NAME
    app.get('/api/pictures/name/:name', function(req, res){
        Picture.find({author: req.params.name}, {name: 1, modified_date: 1},  function(err, pictures){
            if(err) return res.status(500).json({error: err});
            if(pictures.length === 0) return res.status(404).json({error: 'picture not found'});
            res.json(pictures);
        })
    });

    // CREATE PICTURE
    app.post('/api/pictures', function(req, res){
        var picture = new Picture();
        picture.name = req.body.name;

        picture.save((err)=>{
            if(err){
                console.error(err);
                res.json({result: 0});
                return;
            }

            res.json({result: 1});
        });
    });

    // UPDATE THE PICTURE
    app.put('/api/pictures/:name', function(req, res){
        Picture.findById(req.params.name, function(err, picture){
            if(err) return res.status(500).json({ error: 'database failure' });
            if(!picture) return res.status(404).json({ error: 'picture not found' });
    
            if(req.body.name) picture.name = req.body.name;
            if(req.body.modified_date) picture.modified_date = req.body.modified_date;
    
            picture.save(function(err){
                if(err) res.status(500).json({error: 'failed to update'});
                res.json({message: 'picture updated'});
            });
    
        });
    });

    // DELETE PICTURE
    app.delete('/api/pictures/:name', function(req, res){
        Picture.remove({ _id: req.params.name }, function(err, output){
            if(err) return res.status(500).json({ error: "database failure" });
    
            /* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
            if(!output.result.n) return res.status(404).json({ error: "picture not found" });
            res.json({ message: "book deleted" });
            */
    
            res.status(204).end();
        })
    });

}
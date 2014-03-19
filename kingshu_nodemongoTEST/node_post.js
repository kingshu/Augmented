var formidable = require('formidable'),
    http = require('http'),
    util = require('util'),
    fs = require('fs'),
    db = require('mongojs').connect("localhost",["test"]);

http.createServer(function(req, res) {


	db.test.save({email: "srirangan@gmail.com", password: "iLoveMongo", sex: "male"}, function(err, saved) {
	  if( err || !saved ) 
	  	console.log("User not saved");
	  else {
	  	console.log("User saved");
	  	db.test.find({}, function(err, test) {
		  if( err || !test) 
		  	console.log("No a found");
		  else {
		  	console.log("here");
		  	test.forEach( function(a) {
		    	console.log(a);
		  	});
		  }
		});
	  }
	});



  if (req.url == '/upload' && req.method.toLowerCase() == 'post') {
    // parse a file upload
    var form = new formidable.IncomingForm();

    form.parse(req, function(err, fields, files) {
      res.writeHead(200, {'content-type': 'text/plain'});
      res.write('received upload:\n\n');
      fs.rename (files.upload.path, "fsg.jpg", function (err) { 
      	if (err)
      		throw err; 
      	console.log("renamed");
      });

      res.end(util.inspect({fields: fields, files: files}));
    });

    return;
  }

  // show a file upload form
  res.writeHead(200, {'content-type': 'text/html'});
  res.end(
    '<form action="/upload" enctype="multipart/form-data" method="post">'+
    '<input type="text" name="title"><br>'+
    '<input type="file" name="upload" multiple="multiple"><br>'+
    '<input type="submit" value="Upload">'+
    '</form>'
  );
}).listen(8080);
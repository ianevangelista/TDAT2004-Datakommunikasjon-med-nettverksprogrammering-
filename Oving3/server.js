var express = require("express");
var bodyParser = require("body-parser");
const { exec } = require("child_process");
const fs = require("fs");
var path = require("path");
var cors = require("cors");
var app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(express.static("public"));

app.post("/java", (req, res) => {
  console.log("Fikk hent artikler request fra klient");
  console.log(req.body.code);
  fs.writeFile("docker_java/Code.java", req.body.code, function(err) {
    if (err) {
      return console.log(err);
    }
    console.log("The file was saved!");
  });
  exec(
    "docker build -t java_compiler docker_java/.",
    (error, stdout, stderr) => {
      if (error) {
        console.log(`error: ${error.message}`);
        res.status(400);
        res.json({
          output: stdout
            .split("Step 4/5 : RUN javac code.java")[1]
            .substring(
              49,
              stdout.split("Step 4/5 : RUN javac code.java")[1].length - 21
            )
            .replace("[0m[91m", "")
        });
        return;
      }
      if (stderr) {
        console.log(`stderr: ${stderr}`);
        res.status(400);
        res.json({ output: $stderr });
        return;
      }
      console.log(`stdout: ${stdout}`);
      exec("docker run java_compiler", (error, stdout, stderr) => {
        if (error) {
          console.log(`error: ${error.message}`);
          res.status(400);
          res.json({ output: error.message });
          return;
        }
        if (stderr) {
          console.log(`stderr: ${stderr}`);
          res.status(400);
          res.json({ output: stderr });
          return;
        }
        console.log(`stdout: ${stdout}`);
        res.status = 200;
        res.json({ output: stdout });
        return;
      });
    }
  );
});

app.listen(8080);

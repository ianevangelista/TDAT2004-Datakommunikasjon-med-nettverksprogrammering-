import React, { useEffect, useState } from "react";
import { Button, Typography, TextField, Grid } from "@material-ui/core";

const App = () => {
  const [input, setInput] = useState("");
  const [output, setOutput] = useState("");

  useEffect(() => {
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", "./Code.java", false);
    rawFile.onreadystatechange = function() {
      if (rawFile.readyState === 4) {
        if (rawFile.status === 200 || rawFile.status === 0) {
          var allText = rawFile.responseText;
          setInput(allText);
        }
      }
    };
    rawFile.send(null);
  }, []);

  const submit = () => {
    fetch("http://localhost:8080/java", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        code: input
      })
    })
      .then(response => {
        console.log(response);
        return response.json();
      })
      .then(data => {
        setOutput(data.output);
      });
  };

  return (
    <div className="App">
      <Grid style={{ margin: "20px" }}>
        <Typography align="center" variant="h1">
          Øving 3 - online kompilator
        </Typography>
        <Grid container direction="row" justify="center">
          <TextField
            id="input"
            label="Kode"
            multiline
            rows="10"
            variant="outlined"
            fullWidth
            value={input}
            onChange={e => setInput(e.target.value)}
          />
        </Grid>
        <Grid container direction="row" justify="center">
          <Button
            variant="contained"
            onClick={submit}
            style={{ margin: "20px" }}
          >
            Kompiler
          </Button>
        </Grid>
        <Grid container direction="row" justify="center">
          <TextField
            id="output"
            label="Output"
            multiline
            rows="4"
            variant="outlined"
            fullWidth
            value={output}
          />
        </Grid>
      </Grid>
    </div>
  );
};

export default App;

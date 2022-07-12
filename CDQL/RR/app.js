const express = require("express");
const mongoose = require("mongoose");
const app = express();
const port = process.env.PORT_ONE || 7000;
const RRcollections = require("./RRcollections");
const { performance } = require("perf_hooks");
const CRroutes = require("./routes/CR");
const CProutes = require("./routes/CP");
const contextroutes = require("./routes/context");
const feedbackroutes = require("./routes/feedback");

app.use(express.json());

mongoose.connect(
  "mongodb://127.0.0.1:27017/RR",
  { useUnifiedTopology: true, useNewUrlparser: true },
  () => console.log("RR-processor connected")
);

//Global varibles for QoC validation-- to find the relevant RR collections there are used in Post request method to get the request parameters and they are further used in QoC and CoC Evaluator to update the provider's RR value

//To the context provider's SLA body
var cc = "";
var reqetype = "";
var reqlatitude = "";
var reqlongitude = "";
var reqCa = 0;
var reqtimeliness = 0;
var reqcompleteness = 0;
var reqrepresentation = "";
var reqFormat_Ca

//other varibles---to assign the context provider varible at QoC and CoC evaluator, to asses the RR

var invokedCP = "";

//To the CR's body
var reqcctype;
var CRetype;
var CRlatitude;
var CRlongitude;
var reqCRca;
var reqWca;
var reqCRtimeliness;
var reqCRcompleteness;
var reqCRrepresentation;
var reqCRFormat_Ca;
//Routes for the context request
app.use(CRroutes);
app.use(CProutes);
app.use(contextroutes);
app.use(feedbackroutes);

app.listen(port, () => console.log("RR processor at ${port}.."));

//steps, First the request
//Then the Providers (Include All the providers in the collection as the the only providers in discovery hasnt been addressed yet)
//Next invoke and update their RR

const express = require("express");
const router = express.Router();

//CoaaS--context provider's entriy point
//here use findONeandupdatre function to find the above RR collection and update its providers array with this list of providers
router.post("/RRprocessor/context_providers", async (req, res) => {
  const {
    pid,
    cost,
    pen_timeliness,
    pen_completeness,
    pen_representation,
    cetype,
    clatitude,
    clongitude,
    cCa,
    ctimeliness,
    ccompleteness,
    crepresentation,
    cFormat_Ca,
  } = req.body;

  //defining the varibled to pass the values from body so that it can be validated at the QoC CoC evaluator
  reqid = req.body.pid;
  reqetype = req.body.cetype;
  reqlatitude = req.body.clatitude;
  reqlongitude = req.body.clongitude;
  reqCa = req.body.cCa;
  reqtimeliness = req.body.ctimeliness;
  reqcompleteness = req.body.ccompleteness;
  reqrepresentation = req.body.crepresentation;
  cc = reqcctype;
  reqFormat_Ca= req.body.cFormat_Ca
  
  var timevalidity = 0  //varible initialised to compare the 

  //Varible to that holds the CP's existing timeliness-- and the if fucntion defined below chechs wether its compatability with the CC's reuqest
  if (reqCRtimeliness > req.body.ctimeliness) {
     timevalidity = reqCRtimeliness;
  }
  
  
  //check find the relevent RR collection for the context provider
  const RRcmatch = await RRcollections.findOneAndUpdate(
    {
      cctype: cc,
      etype: req.body.cetype,
      latitude: req.body.clatitude,
      longitude: req.body.clongitude,
      Ca: req.body.cCa,
      timeliness: timevalidity,
      completeness: req.body.ccompleteness,
      representation: req.body.crepresentation,
      Format_Ca: req.body.cFormat_Ca
    },

    //adds the providers with the unique ids to RRcollection
    {
      $addToSet: {
        context_providers: [
          {
            pid: req.body.pid,
            for_CC: cc,
            cetype: req.body.cetype,
            clatitude: req.body.clatitude,
            clongitude: req.body.clongitude,
            cCa: req.body.cCa,
            cFormat_Ca: req.body.cFormat_Ca,
            ctimeliness: req.body.ctimeliness,
            ccompleteness: req.body.ccompleteness,
            crepresentation: req.body.crepresentation,
            cost: req.body.cost,
            pen_timeliness: req.body.pen_timeliness,
            pen_completeness: req.body.pen_completeness,
            pen_representation: req.body.pen_representation,
          },
        ],
      },
     
    });

    console.log (RRcmatch)

    if (RRcmatch)
    {
  //code block for finding the similar context provider in the other RR collections updating their OQoC values to the current provider
  const simcp = await RRcollections.findOne({
    etype: req.body.cetype,
    latitude: req.body.clatitude,
    longitude: req.body.clongitude,
    Ca: req.body.cCa,
    timeliness: timevalidity,
    completeness: req.body.ccompleteness,
    representation: req.body.crepresentation,
    Format_Ca: req.body.cFormat_Ca,
    "context_providers.pid": req.body.pid,
  });

  //varibles to hold the existing metrics of the context provider

  var exstnumOQoC;
  var exstsumOQoC;

  //code for fiding and assigning the existing OQoC values
  for (var i = 0; i < simcp.context_providers.length; i++) {
    if (simcp.context_providers[i].pid == req.body.pid) {
      exstnumOQoC = simcp.context_providers[i].numOQoC;
      exstsumOQoC = simcp.context_providers[i].sumQoC;
      break;
    }
  }

  const updatecp = await RRcollections.updateMany(
    { "context_providers.pid": req.body.pid },
    {
      $set: {
        "context_providers.$.sumOQoC": exstsumOQoC,
        "context_providers.$.numOQoC": exstnumOQoC,
      },
    }
  );

  console.log("potential context providers and their QoC metrics are updated");

  //Finding providers with the highest RR of the discovered  for Assurance is the providers with highRR values
  const highRRp = await RRcollections.findOne({
    cctype: cc,
    etype: req.body.cetype,
    latitude: req.body.clatitude,
    longitude: req.body.clongitude,
    Ca: req.body.cCa,
    timeliness: timevalidity,
    completeness: req.body.ccompleteness,
    representation: req.body.crepresentation,
    Format_Ca: req.body.cFormat_Ca
  });
  const highcp = highRRp.context_providers;
  const highRRvalue = Math.max.apply(
    Math,
    highcp.map(function (o) {
      return o.RRvalue;
    })
  );

  var elig = [];
  var forAssurance = [];
  for (var i = 0, len = highcp.length; i < len; i++) {
    if (highcp[i].RRvalue == highRRvalue) {
      forAssurance.push(highcp[i]); //this is the metric for assurance processor
      elig.push(highcp[i].pid);
    } //list of providers to QoC and CoC evaluator (not required for the journal paper's evaluation) (is this required? if not remove)
  }

  //Assurance***********************************************************************************************************************************************************************************
  var nocostpenalties = [];
  var costnopenalties = []; //(are these varible required? if not, remove..)
  var costandpenalties = [];

  //write and if loop stating that if cost and penalties are NaN the rest.send(forAssurance as the eligible providers)
  //else if the below code
  var p1 = []; //providers for sorting
  var p2 = [];
  //this for finding out the minimum cost in the  providers for notmalising their values to compute for MCDM
  var minCost = Math.min.apply(
    Math,
    forAssurance.map(function (o) {
      return o.cost;
    })
  );
  var maxTim = Math.max.apply(
    Math,
    forAssurance.map(function (o) {
      return o.pen_timeliness;
    })
  );
  var maxComp = Math.max.apply(
    Math,
    forAssurance.map(function (o) {
      return o.pen_completeness;
    })
  );
  var maxRep = Math.max.apply(
    Math,
    forAssurance.map(function (o) {
      return o.pen_representation;
    })
  );

  //normalization for MCDM
  //noramlised cost, timeliness, completeness and representation

  var normalisedcp = [];

  for (var i = 0, len = forAssurance.length; i < len; i++) {
    forAssurance[i].cost = minCost / forAssurance[i].cost;
    forAssurance[i].pen_timeliness = forAssurance[i].pen_timeliness / maxTim;
    forAssurance[i].pen_completeness =
      forAssurance[i].pen_completeness / maxComp;
    forAssurance[i].pen_representation =
      forAssurance[i].pen_representation / maxRep;
    normalisedcp.push(forAssurance[i]);
  }

  //assigning weights to normalised matrix
  /*For our computations the weights attained for our use-case are 
        C = 0.6126     T= 0.2318   C= 0.1201    R = 0.0325
        */

  var weightedcp = [];

  //measuring the weighted sum and also if any other SLA values doesnt exist from the providerside they are considered as zero for their inclusion in the computation

  for (var i = 0, len = normalisedcp.length; i < len; i++) {
    normalisedcp[i].cost = 0.6126 * forAssurance[i].cost;
    normalisedcp[i].cost = normalisedcp[i].cost || 0;

    normalisedcp[i].pen_timeliness = 0.2318 * forAssurance[i].pen_timeliness;
    normalisedcp[i].pen_timeliness = normalisedcp[i].pen_timeliness || 0;

    normalisedcp[i].pen_completeness =
      0.1201 * forAssurance[i].pen_completeness;
    normalisedcp[i].pen_completeness = normalisedcp[i].pen_completeness || 0;

    normalisedcp[i].pen_representation =
      0.0325 * forAssurance[i].pen_representation;
    normalisedcp[i].pen_representation =
      normalisedcp[i].pen_representation || 0;

    weightedcp.push(normalisedcp[i]);
  }

  //compute perfromance scores
  var perfromance_scores = [];

  for (var i = 0, len = normalisedcp.length; i < len; i++) {
    perfromance_scores.push(
      weightedcp[i].cost +
        weightedcp[i].pen_timeliness +
        weightedcp[i].pen_completeness +
        weightedcp[i].pen_representation
    );
  }

  //sorting based on the performance scores

  for (var i = 0; i < perfromance_scores.length; i++) {
    for (let n = 0; n < perfromance_scores.length - i - 1; n++) {
      p1 = perfromance_scores[n + 1];
      p2 = perfromance_scores[n];
      if (p1 > p2) {
        [perfromance_scores[n + 1], perfromance_scores[n]] = [
          perfromance_scores[n],
          perfromance_scores[n + 1],
        ];
        [weightedcp[n + 1], weightedcp[n]] = [weightedcp[n], weightedcp[n + 1]];
      }
    }
  }

  //Remove this comment and write Assurance cache function here
  console.log(
    "Eligible context providers are selected and sorted based on the cost effectiveness"
  );

  res.send(
    weightedcp.map(function (o) {
      return o.pid;
    })
  );
  }
  else{
    res.send("please enter a valid context provider or check the context request")
  }
  
});

module.exports = router;

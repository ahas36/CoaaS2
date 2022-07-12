const express = require("express");
const { findOne } = require("../RRcollections");
const router = express.Router();

router.post("/QoC_CoC_Evaluator/feedback", async (req, res) => {
  //context consumers Feedback along with their id
  const { ccid, RRunit } = req.body;

  //finding and updating the RR collection with the RR metrics provided by the context consumer

  //the below fuction updates the numRRunits of the context provider

  const RRcollectionfeed = await RRcollections.findOneAndUpdate(
    {
      cctype: req.body.ccid,
      etype: CRetype,
      latitude: CRlatitude,
      longitude: CRlongitude,
      Ca: reqCRca,
      timeliness: reqCRtimeliness,
      completeness: reqCRcompleteness,
      representation: reqCRrepresentation,
      "context_providers.pid": invokedCP,
    },
    { $inc: { "context_providers.$.numRRunits": 1 } }
  );

  //the below function updates the sumRRunits of the context provider

  const collection_for_feedback = await RRcollections.findOne({
    cctype: req.body.ccid,
    etype: CRetype,
    latitude: CRlatitude,
    longitude: CRlongitude,
    Ca: reqCRca,
    timeliness: reqCRtimeliness,
    completeness: reqCRcompleteness,
    representation: reqCRrepresentation,
    "context_providers.pid": invokedCP,
  });

  var sumQoC //varible to store sum OQoC of context provider for RR assessment 

  var existingsumRR = 0;

  for (var i = 0; i < collection_for_feedback.context_providers.length; i++) {
    if (collection_for_feedback.context_providers[i].pid === invokedCP) {
      existingsumRR = collection_for_feedback.context_providers[i].sumRRunits;
      sumQoC = collection_for_feedback.context_providers[i].sumOQoC;
      break;
    }
  }
  console.log(sumQoC)
  var newRRsum = existingsumRR + req.body.RRunit;
  var RR = (newRRsum + sumQoC)/2

  const updatedRR = await RRcollections.findOneAndUpdate(
    {
      cctype: req.body.ccid,
      etype: CRetype,
      latitude: CRlatitude,
      longitude: CRlongitude,
      Ca: reqCRca,
      timeliness: reqCRtimeliness,
      completeness: reqCRcompleteness,
      representation: reqCRrepresentation,
      "context_providers.pid": invokedCP,
    },
    { $set: { "context_providers.$.sumRRunits": newRRsum } }
  );
  console.log("RR metrics are updated");

  //code to update the context provider's RR value 
  const updatedRRvalue = await RRcollections.findOneAndUpdate(
    {
      cctype: req.body.ccid,
      etype: CRetype,
      latitude: CRlatitude,
      longitude: CRlongitude,
      Ca: reqCRca,
      timeliness: reqCRtimeliness,
      completeness: reqCRcompleteness,
      representation: reqCRrepresentation,
      "context_providers.pid": invokedCP,
    },
    { $set: { "context_providers.$.RRvalue": RR } }
  );




  res.send("feedback recieve and RR has been updated");
});

module.exports = router;

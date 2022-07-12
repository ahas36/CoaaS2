const express = require("express");
const router = express.Router();

//CoaaS--request entry point
router.post("/RRprocessor/context_requests", async (req, res) => {
  //Assigning the data to the varibles form the incoming requests
  const {
    cctype,
    etype,
    latitude,
    longitude,
    Ca,
    Wca, 
    Format_Ca,
    timeliness,
    completeness,
    representation,
  } = req.body;

  //passing the required values to QoC and CoC evaluator
  reqcctype = req.body.cctype;
  CRetype = req.body.etype;
  CRlatitude = req.body.latitude;
  CRlongitude = req.body.longitude;
  reqCRca = req.body.Ca;
  reqWca = req.body.Wca;
  reqCRFormat_Ca = req.body.Format_Ca,
  reqCRtimeliness = req.body.timeliness;
  reqCRcompleteness = req.body.completeness;
  reqCRrepresentation = req.body.representation;


  //check if this context request (in JSON body must include all the elements e.g., cctype... as present in below await function to avoid the duplicates)
  const etypeexists = await RRcollections.findOne({
    cctype: req.body.cctype,
    etype: req.body.etype,
    latitude: req.body.latitude,
    longitude: req.body.longitude,
    Ca: req.body.Ca,
    Wca: req.body.Wca,
    Format_Ca: req.body.Format_Ca, 
    timeliness: req.body.timeliness,
    completeness: req.body.completeness,
    representation: req.body.representation,
  });

console.log(etypeexists)
  if (etypeexists) {
    res.send("RR collection exists");
    console.log("a similar CR exists as RR collection..");
  }

  //if not create the data
  else {
    const newRRcollection = new RRcollections({
      cctype,
      etype,
      latitude,
      longitude,
      Ca,
      Wca,
      Format_Ca, 
      timeliness,
      completeness,
      representation,
    });
    newRRcollection.save();
    console.log("a CR has been initiated!");
    return res.json(newRRcollection);
  }
});

module.exports = router;

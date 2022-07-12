const express = require("express");
const router = express.Router();

//************************************************************ QoC and CoC Evaluator**********************************************************************************************************************************
//to avoid the inconsistancies lets reduce the RR if any objective QoC has been violated but for subjective QoC lets cosnider the providers id along with the consumer's id as params to reduce the RR

//varibles for reference (Don't uncomment)
/*reqetype;
        reqlatitude;   
        reqlongitude;                    (From the SLA's body)
        reqCa;
        reqtimeliness;
        reqcompleteness; 
        reqrepresentation*/

//reqcctype                       (From request's body)

router.post("/QoC_CoC_Evaluator/context_responses", async (req, res) => {
  const { id, Ca, ts, age } = req.body;

  //varibles to compute the acquired timeliness, completeness and representation

  var timeliness = 10;
  var completeness = 1;
  var representation = 1;
  var OQoC = 0;

  //assignment of 'id' to a varible for passing it to RR processor

  //Additional varibles to compute the completeness based on the weights
  /*var Swca = 0                                    //for sum of weights defined in the context request 
        var Swpca = 0                                   //for sum of weights in the delivered context response
        
           //computing the sum of weights requested by the context consumer 
           Swca = reqWca.reduce((a,b) => a+b,0)
           console.log(Swca)*/

  //write the code to compute based on the weights

  //finding the invoked context provider in the RR collection and retrieving the context provider's SLA details for QoC computation
  //varibles that hold the context providr's QoC gaurantees
  var time_SLA = 0;
  var comp_SLA = 0;
  var rep_SLA = 0;

  //varible that holds the invoked CP's id
  invokedCP = req.body.id;

  const QoCn = await RRcollections.findOne({
    cctype: cc,
    etype: CRetype,
    latitude: CRlatitude,
    longitude: CRlongitude,
    Ca: reqCRca,
    timeliness: reqCRtimeliness,
    completeness: reqCRcompleteness,
    representation: reqCRrepresentation,
    "context_providers.pid": req.body.id,
  });

  if (QoCn) {
    //find the provider's timeliness value (add the context provider's SLA details to the RR collection; loop throght all the required ememts by seperating with &&; and acquire the penalty values)
    for (var i = 0; QoCn.context_providers.length; i++) {
      if (QoCn.context_providers[i].pid === req.body.id) {
        time_SLA = QoCn.context_providers[i].ctimeliness;
        comp_SLA = QoCn.context_providers[i].cCa.length;

        break;
      }
    }

    console.log("QoC gaurantees in SLA are collected");

    //computing the metrics and OQoC value using this value we update the reputation, later compute the context costs
    if (req.body.age > 0.01 && req.body.age < time_SLA) {
      timeliness = 1 - req.body.age / time_SLA; //need to update to compute the timeliness based on the higher value assigned by the context provider
    } else if (req.body.age <= 0.01) {
      timeliness = 1;
    } else if (req.body.age >= time_SLA) {
      timeliness = 0;
    }

    //update the formula as above to compute timeliness based on different constrains

    if (req.body.ts) {
      timeliness = 1 - (Date.now() / 1000 - ts / 1000) / time_SLA;
    }

    // updated code to compute the completeness

    // varible to compute the compute completeness to store the weight of context attribute present - when the length of the context attributes are less than the length of the required

    var sumofWeight = 0;

    // context request's required context
    reqCRca;
    // required weights of context
    reqWca;
    //context response
    Ca;
    // Compare the object of context attibutes with each context attribute in the respone


        //**** varibles defined to compute the representation format  ***********************\\
           var Dfca = 0


    var obj = Ca; //varible to obtain the key values of Ca to assess the completeness
    var keys = Object.keys(obj);
    var values = Object.values(obj);

    if (Object.keys(Ca).length == comp_SLA) {
      completeness = 1;
}
  

    // if such match does occurs add the value to the completeness sum

    //else add "0" to the completeness sum

    if (Object.keys(Ca).length < comp_SLA) {
      for (var i = 0; i < reqCRca.length; i++) {
        for (var j = 0; j < keys.length; j++) {
          if (reqCRca[i] == keys[j]) {
            sumofWeight = sumofWeight + reqWca[i];
          } else {
            console.log(keys[0]);
            sumofWeight = sumofWeight + 0;
          }
        }
      }
      completeness = sumofWeight / reqcompleteness; //reqcompleteness is context provider's completeness gautantee on their SLA
    }
    else if (Object.keys(Ca).length == 0)
    {
      completeness = 0
    }
    
    //Repreresentation - when the context contains all the Cas given on the SLA

  if (values.length != 0){  
    for (var i = 0; i< reqCa.length; i++){
      for (var j = 0; j < values.length; j++) {

        if (reqCa[i] == keys[j]){
        if( reqFormat_Ca[i] == typeof values[j]) {
          
          Dfca = Dfca + 1
      }
    }
      else {
      representation = 0 
      }
      
}
}
representation = Dfca/reqCa.length
}

console.log(timeliness, completeness, representation)


    //representation = Object.keys(Ca) / comp_SLA; //needs a change - also can incorporate the computation of representation using integers?
    // do a type check in the context responses
    // compare the context responses type with the SLA's
    // Then measure the representation using the formula DCa/Ca

    OQoC = (timeliness + completeness + representation) / 3;

    console.log("QoC is computed based on the provisioned metadata");

    //update provider's reputation in all RR collection that context provider belong

    const numQoC = await RRcollections.updateMany(
      { "context_providers.pid": req.body.id },
      { $inc: { "context_providers.$.numOQoC": 1 } }
    );

    const existingR = await RRcollections.findOne({
      "context_providers.pid": req.body.id,
    });

    //Find the exising context provider and their OQoC
console.log(existingR)
    var existingOQoC = 0;

    for (var i = 0; i < existingR.context_providers.length; i++) {
      if (existingR.context_providers[i].pid === req.body.id) {
        exisingOQoC = existingR.context_providers[i].sumOQoC;
        break;
      }
    }
    var newOQoC = existingOQoC + OQoC;

    const updatedOQoC = await RRcollections.updateMany(
      { "context_providers.pid": id },
      { $set: { "context_providers.$.sumOQoC": newOQoC } }
    );
    console.log("RR metrics are updated");

    //cost computation by mapping the existing pentalites

    var timeliness_difference = 0;
    var completeness_difference = 0;
    var representation_difference = 0;

    var pent = 0;
    var penc = 0;
    var penr = 0;

    var final_timeliesss_penalty = 0;
    var final_completeness_penalty = 0;
    var final_representation_penalty = 0;
    var final_pen = 0;

    var final_cost = 0;

    //penalties for timeliness
    timeliness_difference = 1 - timeliness;

    //penalties for completeness
    completeness_difference = 1 - completeness;

    //penalties for representation
    representation_differnce = 1 - representation;

    const cp = await RRcollections.findOne({ "context_providers.pid": reqid });

    //find the provider's timeliness value (add the context provider's SLA details to the RR collection; loop throght all the required ememts by seperating with &&; and acquire the penalty values)
    for (var i = 0; cp.context_providers.length; i++) {
      if (cp.context_providers[i].pid === req.body.id) {
        pent = cp.context_providers[i].pen_timeliness;
        penc = cp.context_providers[i].pen_completeness;
        penr = cp.context_providers[i].pen_representation;

        console.log("penalties collected");

        final_timeliness_penalty = timeliness_difference * (pent / 10);
        final_completeness_penalty = completeness_difference * (penc / 10);
        final_representation_penalty = representation_difference * (penr / 10);

        final_pen =
          final_timeliness_penalty +
          final_completeness_penalty +
          final_representation_penalty;

        final_cost = cp.context_providers[i].cost - final_pen;
        console.log("penalties are computed");
        break;
      }
    }
    res.send("Will repurn the provider's id, updated RR, penalties soon");
  } else {
    res.send("something went wrong while recieving the context response");
  }

  //these are for using at the assurance caching ard req validation

  /*if (reqCRtimeliness != undefined && reqCRcompleteness != undefined && reqCRrepresentation != undefined) //perform validation for all timeliness, completeness and representaton 
        
              if(req.body.age >= reqtimeliness || req.body.Ca.length < reqcompleteness || typeof req.body.Ca != object)
              {
                  res.send ("service disruption! Invalid context..")
              }
              else 
                 res.send (res.body.Ca)
               
        if (reqCRtimeliness != undefined && reqCRcompleteness != undefined && reqCRrepresentation === undefined) //perform validation only for timeliness and completeness 
               
                if(req.body.age >= reqtimeliness || req.body.Ca.length < reqcompleteness)
                {
                    res.send ("service disruption! Invalid context..")
                }
                else
                { 
                   res.send (res.body.Ca)
                }    
        if (reqCRtimeliness != undefined && reqCRcompleteness === undefined && reqCRrepresentation === undefined) //perform validation only for timeliness 
                if(req.body.age >= reqtimeliness)
                {
                    res.send ("service disruption! Invalid context..")
                }
                else
                { 
                res.send (res.body.Ca)
                }   
        
        if (reqCRtimeliness === undefined && reqCRcompleteness === undefined && reqCRrepresentation === undefined)  //inthis case cacheing is not needed
                {
                res.send (res.body.Ca)
                }*/

  //check for timeliness, completeness and representation validity and assign NUMRRunits and sum RRunits and Cost computation for Assurance here can (This can be later)
  /*if(req.body.age >= reqtimeliness || req.body.Ca.length < reqcompleteness) //for all RR collections 
        {
           
            const reduceRR = await RRcollections.updateMany({etype : reqetype, latitude : reqlatitude, longitude: reqlongitude, Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
                                                                   {$inc:{"context_providers.$.numRRunits":1}}) 
                                                                   
                                                                                                       
        }
        else 
        {
            const increaseRRnum = await RRcollections.updateMany({etype : reqetype, latitude : reqlatitude, longitude: reqlongitude, Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
            {$inc:{"context_providers.$.numRRunits":1} })  
            const increasesumRR = await RRcollections.updateMany({etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
            {$inc:{"context_providers.$.sumRRunits":1}})
        
            
        }    */

  //check and update the RR valus for subjective QoC

  /*if((req.body.id != "video_camera" && reqcctype == "doctor") || (req.body.id != "thermal_camera" && reqcctype == "teen")) {
          const reduce1RR = await RRcollections.findOneAndUpdate({cctype: reqcctype, etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca: reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
          {$inc:{"context_providers.$.numRRunits":1}})        
        }
        else 
        {
            const increaseRRnum = await RRcollections.findOneAndUpdate({cctype: reqcctype, etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
            {$inc:{"context_providers.$.numRRunits":1} })  
            const increasesumRR = await RRcollections.findOneAndUpdate({cctype: reqcctype, etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
            {$inc:{"context_providers.$.sumRRunits":1}})
        }*/

  /*const RRval1 = await RRcollections.findOne({cctype: reqcctype, etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id})
        var i = 0;
        
        for(i = 0; i<RRval1.context_providers.length; i++){
        console.log("reach")
                if(RRval1.context_providers[i].pid == req.body.id){
                    RRvalue = RRval1.context_providers[i].sumRRunits/RRval1.context_providers[i].numRRunits
                break;
                }
        }
        
        const Rvae1 = await RRcollections.findOneAndUpdate({etype : reqetype, latitude : reqlatitude, longitude: reqlongitude,Ca :reqCa, timeliness :  reqtimeliness, completeness :  reqcompleteness, representation : reqrepresentation, "context_providers.pid": id},
            {$set:{"context_providers.$.RRvalue":RRvalue}})*/
});

module.exports = router;

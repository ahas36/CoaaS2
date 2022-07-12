const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const RRcollectionsSchema = new Schema({
  cctype: { type: String, default: "" }, //context request
  etype: { type: String, default: "" },
  latitude: { type: String, default: "" },
  longitude: { type: String, default: "" },
  Ca: [{ type: String }],
  Wca: [{ type: Number, default: 0 }], 
  Format_Ca: [{ type: String}],
  timeliness: { type: Number, default: 10 },
  completeness: { type: Number, default: 0 },
  representation: {type: String},

  context_providers: [
    {
      pid: { type: String, unique: false }, //context provider
      for_CC: { type: String },

      cetype: { type: String }, //SLA details
      clatitude: { type: String },
      clongitude: { type: String },
      cCa: [{ type: String }],
      cFormat_Ca: [{ type: String}],
      ctimeliness: { type: Number },
      ccompleteness: { type: Number },
      crepresentation: { type: String},

      cost: { type: Number, default: 0 }, //RR details
      pen_timeliness: { type: Number, default: 0 },
      pen_completeness: { type: Number, default: 0 },
      pen_representation: { type: Number, default: 0 },
      sumOQoC: { type: Number, default: 0 },
      numOQoC: { type: Number, default: 0 },
      sumRRunits: { type: Number, default: 0 },
      numRRunits: { type: Number, default: 0 },
      RRvalue: { type: Number, default: 1 },
      _id: false,
    },
  ],

  created_at: { type: Date, default: Date.now() },
});

module.exports = RRcollections = mongoose.model("RRc", RRcollectionsSchema);

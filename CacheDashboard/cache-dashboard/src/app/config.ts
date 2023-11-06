export var config = {
    "refresh_rate": 60000,
    "uri": "http://localhost:8070/CASM-2.0.1/api/log/performance",
    "model_uri": "http://localhost:8070/CASM-2.0.1/api/log/models",
    "querystaturi": "http://localhost:8070/CASM-2.0.1/api/cqstats",
    "scenario": "hazard", // parking, hazard
    // For parking scenario
    "carparks": "https://demo0724795.mockable.io/carparks",
    "places": "https://demo0724795.mockable.io/places",
    // For hazard scenario
    "bikes": "http://localhost:5000/bicycles",
    "cars": "http://localhost:5000/cars",
    "hazards": "http://localhost:8070/CASM-2.0.1/api/log/hazards"
}

// export var config = {
//     "refresh_rate": 60000,
//     "uri": "http://demo0724795.mockable.io/log/performance",
//     "model_uri": "http://demo0724795.mockable.io/api/log/models",
//     "carparks": "https://demo0724795.mockable.io/carparks",
//     "places": "https://demo0724795.mockable.io/places",
//     "querystaturi": "http://demo0724795.mockable.io/cqstats"
// }

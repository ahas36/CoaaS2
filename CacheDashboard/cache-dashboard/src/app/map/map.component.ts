import { Component, OnInit } from '@angular/core';
import { Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip } from 'ng2-charts';
import { ApiServiceService } from '../services/api-service.service';
import { config } from '../config';
import { ChartDataSets, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})

export class MapComponent implements OnInit {
  ishazard;

  queries;
  timeTicks;
  queryLocations;

  zoom: number = 15;
  lat: number = -37.818807405859864
  lng: number = 144.96796979164372;

  parkmarkers: marker[] = [];
  placemarkers: marker[] = [];
  querymarkers: queryMarker[] = [];
  
  carmarkers: mobileMarker[] = [];
  redbikemarkers: mobileMarker[] = [];
  greenbikemarkers: mobileMarker[] = [];
  
  public queryLoadVariation: ChartDataSets[] = [];
  public lineChartType = 'line';
  public lineChartLabels: Label[];
  public chartOption: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Number'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Window Index'
        }
      }]
    }
  };
  public lineChartLegend = true;
  public chartPlugins = [];

  interval:any;

  constructor(private serviceAPI: ApiServiceService) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() {
    this.ishazard = (config.scenario == 'hazard')
    if(this.ishazard) {
      this.lat = -37.81300076003162; 
      this.lng = 144.98572470373261;
      this.zoom = 17;
    }

    this.initializeData();
    this.interval = setInterval(() => {
      this.updateData();
  }, config.refresh_rate);
  }

  initializeData(){
    try{
      let data = this.serviceAPI.getQueryLoadVariation();

      this.queries = data.perf.query_load.get();
      this.queryLoadVariation.push({ data: this.queries, label: 'Context Queries' });
      this.timeTicks = data.perf.timeTicks.get();
      

      if(config.scenario === 'parking') {
        this.queryLocations = data.query;
        let carparks = this.serviceAPI.getCarParks();
        let places = this.serviceAPI.getPlaces();

        // Show the context query locations.
        this.queryLocations.subscribe(locs => {
          for(const element of locs){
            this.querymarkers.push({
                lat: element.location.lat,
                lng: element.location.lng,
                dest: element.address,
                icon: {
                  url: './assets/query.gif',
                  scaledSize: {
                      width: 50,
                      height: 50
                  }
              }});
            }
        });
        
        // Shows the car parking facilities.
        carparks.subscribe(parks => {
          for(const element of parks){
  
            this.parkmarkers.push({
              lat: element.location.lat,
              lng: element.location.long,
              label: element.name,
              address: element.address,
              rating: element.rating,
              icon: {
                  url: './assets/parking.png',
                  scaledSize: {
                      width: 20,
                      height: 20
                  }
              }
            });
          }
        });
        
        // Shows the buildings/locations around.
        places.subscribe(place => {
          for(const element of place){
  
            this.placemarkers.push({
              lat: element.geometry.location.lat,
              lng: element.geometry.location.lng,
              label: element.name,
              address: element.formatted_address,
              rating: element.rating,
              icon: {
                url: './assets/building.png',
                scaledSize: {
                    width: 23,
                    height: 23
                }
            }
            });
          }
        });
      }
      else if(config.scenario === 'hazard'){
        this.updateHazardData();
      }
    }
    catch(ex){
      console.log('An error occured!'+ ex);
    }
  }

  clearSubs() {
    this.carmarkers = [];
    this.redbikemarkers = [];
    this.greenbikemarkers = [];
  }

  updateHazardData(isRefresh=false) {
    let hazardata = this.serviceAPI.getHarzadsCarsBikes();
    if(isRefresh) {
      this.clearSubs();
    } 

    // Cars
    for(const element of hazardata.cars){
      if(element.speed > 0){
        this.carmarkers.push({
          lat: element.latitude,
          lng: element.longitude,
          speed: element.speed,
          heading: element.heading,
          rego: element.vin,
          hazardLevel: 0.0,
          icon: {
            url: './assets/car-moving.png',
            scaledSize: {
                width: 23,
                height: 23
            }
        }
        });
      } else {
        this.carmarkers.push({
          lat: element.latitude,
          lng: element.longitude,
          speed: 0,
          heading: element.heading,
          rego: element.vin,
          hazardLevel: 0.0,
          icon: {
            url: './assets/car-stopped.png',
            scaledSize: {
                width: 23,
                height: 23
            }
        }
        });
      }
    }

    // Not in danger bikes
    for(const element of hazardata.greens){
      this.greenbikemarkers.push({
        lat: element.latitude,
        lng: element.longitude,
        speed: element.speed,
        heading: element.heading,
        rego: element.vin,
        hazardLevel: 0.0,
        icon: {
          url: './assets/bike-safe.png',
          scaledSize: {
              width: 23,
              height: 23
          }
      }
      });
    }
    // Endangered bikes
    for(const element of hazardata.reds){
      this.redbikemarkers.push({
        lat: element.latitude,
        lng: element.longitude,
        speed: element.speed,
        heading: element.heading,
        rego: element.vin,
        hazardLevel: element.hazardLevel,
        icon: {
          url: './assets/bike-unsafe.png',
          scaledSize: {
              width: 23,
              height: 23
          }
      }
      });
    }
  }

  updateData() {
    try{
      let data = this.serviceAPI.getQueryLoadVariation();
      this.queries = data.perf.query_load.get();
      this.queryLoadVariation[0].data = this.queries;
      this.timeTicks = data.perf.timeTicks.get();
      if(config.scenario === 'parking') {
        this.queryLocations = data.query;
      }
      else {
        this.updateHazardData(true);
      }
    }
    catch(ex){
      console.log('An error occured!'+ ex);
    }
  }
}

interface marker {
	lat: number;
	lng: number;
	label: string;
  address: string;
  rating: number;
  icon: object;
}

interface queryMarker {
  lat: number;
	lng: number;
  icon: object;
  dest: string
}

interface mobileMarker {
  lat: number;
	lng: number;
  speed: number;
  heading: number;
  rego: string;
  icon: object;
  hazardLevel: number;
}




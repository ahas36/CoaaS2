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

  queries;
  timeTicks;
  queryLocations;

  zoom: number = 15;
  lat: number = -37.818807405859864
  lng: number = 144.96796979164372;

  parkmarkers: marker[] = [];
  placemarkers: marker[] = [];
  querymarkers: queryMarker[] = [];

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
    this.initializeData();
    this.interval = setInterval(() => {
      this.updateData();
  }, config.refresh_rate);
  }

  initializeData(){
    try{
      let carparks = this.serviceAPI.getCarParks();
      let places = this.serviceAPI.getPlaces();

      let data = this.serviceAPI.getQueryLoadVariation();

      this.queries = data.perf.query_load.get();
      this.queryLoadVariation.push({ data: this.queries, label: 'Context Queries' });
      this.timeTicks = data.perf.timeTicks.get();
      this.queryLocations = data.query;

      carparks.subscribe(parks => {
        for(let i=0; i < parks.length; i++){

          this.parkmarkers.push({
            lat: parks[i].location.lat,
            lng: parks[i].location.long,
            label: parks[i].name,
            address: parks[i].address,
            rating: parks[i].rating,
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

      places.subscribe(place => {
        for(let i=0; i < place.length; i++){

          this.placemarkers.push({
            lat: place[i].geometry.location.lat,
            lng: place[i].geometry.location.lng,
            label: place[i].name,
            address: place[i].formatted_address,
            rating: place[i].rating,
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

      queryLocations.subscribe(locs => {
        for(let i=0; i < locs.length; i++){
          this.querymarkers.push({
              lat: locs[i].location.lat,
              lng: locs[i].location.lng,
              dest: locs[i].address,
              icon: {
                url: './assets/query.gif',
                scaledSize: {
                    width: 50,
                    height: 50
                }
            }});
          }
      });
    }
    catch(ex){
      console.log('An error occured!'+ ex);
    }
  }

  updateData() {
    try{
      let data = this.serviceAPI.getQueryLoadVariation();
      this.queries = data.perf.query_load.get();
      this.queryLoadVariation[0].data = this.queries;
      this.timeTicks = data.perf.timeTicks.get();
      this.queryLocations = data.query;
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
}




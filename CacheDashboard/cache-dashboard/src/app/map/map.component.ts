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
   
      // this.queryLocations.subscribe(locs => {
      //   for(const element of locs){
      //     this.querymarkers.push({
      //         lat: element.location.lat,
      //         lng: element.location.lng,
      //         dest: element.address,
      //         icon: {
      //           url: './assets/query.gif',
      //           scaledSize: {
      //               width: 50,
      //               height: 50
      //           }
      //       }});
      //     }
      // });

      this.querymarkers.push({
        lat: -37.825901243583886,
        lng: 144.95778574794554,
        dest: "Royal Botanic Gardens Victoria - Melbourne Gardens",
        icon: {
          url: './assets/query.gif',
          scaledSize: {
              width: 50,
              height: 50
          }
      }});


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
  dest: string
}




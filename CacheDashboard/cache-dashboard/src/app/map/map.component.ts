import { Component, OnInit } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})

export class MapComponent implements OnInit {
  
  zoom: number = 8;
  lat: number = -37.818807405859864
  lng: number = 144.96796979164372;

  markers: marker[] = [];

  constructor(private serviceAPI: ApiServiceService) {}

  ngOnInit() {
    this.initializeData();
  }

  initializeData(){
    try{
      let carparks = this.serviceAPI.getCarParks();
      let places = this.serviceAPI.getPlaces;

      for(let i=0; i < carparks.length; i++){
        this.markers.push({
          lat: carparks[i].location.lat,
          lng: carparks[i].location.long,
          label: carparks[i].name
        });
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
}




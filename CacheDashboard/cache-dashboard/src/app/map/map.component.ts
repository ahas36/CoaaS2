import { Component, OnInit } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})

export class MapComponent implements OnInit {
  
  zoom: number = 15;
  lat: number = -37.818807405859864
  lng: number = 144.96796979164372;

  parkmarkers: marker[] = [];
  placemarkers: marker[] = [];

  constructor(private serviceAPI: ApiServiceService) {}

  ngOnInit() {
    this.initializeData();
  }

  initializeData(){
    try{
      let carparks = this.serviceAPI.getCarParks();
      let places = this.serviceAPI.getPlaces();

      carparks.subscribe(parks => {
        for(let i=0; i < parks.length; i++){

          this.parkmarkers.push({
            lat: parks[i].location.lat,
            lng: parks[i].location.long,
            label: parks[i].name,
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
    catch(ex){
      console.log('An error occured!'+ ex);
    }
  }
}

interface marker {
	lat: number;
	lng: number;
	label: string;
  icon: object;
}




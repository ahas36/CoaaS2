import { Component, OnInit, Injectable } from '@angular/core';
import { config } from '../config';
import { RefreshService } from '../refreshservice/refresh.service';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  overall = true;
  cache_level = false;
  csms = false;
  map = false;

  interval;

  constructor(private api_service:ApiServiceService, private refresh_service: RefreshService) {
    this.api_service.retrievePerformanceData();
  }
  
  ngOnInit() {
    this.interval = setInterval(() => { 
      this.api_service.retrievePerformanceData();
  }, config.refresh_rate);
  }

  getOverallPerformance(){
    this.reset()
    this.overall=true
  }

  getCacheLevelBasedPerfromance(){
    this.reset()
    this.cache_level=true
  }

  getCSMSPerformance(){
    this.reset()
    this.csms=true
  }

  getMap(){
    this.reset()
    this.map=true
  }

  reset(){
    this.overall=false
    this.cache_level=false
    this.csms=false
    this.map=false
  }

}

import { Component, OnInit, Injectable } from '@angular/core';
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

  constructor(private api_service:ApiServiceService) {
    this.api_service.retrievePerformanceData();
  }
  
  ngOnInit() {}

  triggerRefreshing(){
    while(true){
      setTimeout(() => this.api_service.retrievePerformanceData(), 60*1000);
    }
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

  reset(){
    this.overall=false
    this.cache_level=false
    this.csms=false
  }

}

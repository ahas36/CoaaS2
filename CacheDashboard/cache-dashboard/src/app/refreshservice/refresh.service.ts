import { Injectable } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';
import { config } from '../config';

@Injectable()
export class RefreshService {
  constructor(private api_service:ApiServiceService) { 
    // this.triggerRefreshing();
  }

  triggerRefreshing(){
    setInterval(() => { 
      this.api_service.retrievePerformanceData();
      }, config.refresh_rate);
    
    if(config.scenario === 'hazards') {
      setInterval(() => { 
        this.api_service.hazardCarsBikeRoutine();
        }, 5000);
    }
  }
} 
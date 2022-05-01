import { Injectable } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Injectable()
export class RefreshService {
  constructor(private api_service:ApiServiceService) { 
    // this.triggerRefreshing();
  }

  triggerRefreshing(){
    while(true){
      setTimeout(() => {
          this.api_service.retrievePerformanceData(); 
    }, 1*1000);
    }
  }
} 
import { Component, OnInit } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-cachelevels',
  templateUrl: './cachelevels.component.html',
  styleUrls: ['./cachelevels.component.css']
})
export class CacheLevelsComponent implements OnInit {

  localtotalcases

  constructor(private serviceAPI: ApiServiceService) {
    this.serviceAPI.getCacheLevelPerformanceData().subscribe(
      data => {
        this.localtotalcases=data.levels
      }
    )
  }

  ngOnInit() {}

}

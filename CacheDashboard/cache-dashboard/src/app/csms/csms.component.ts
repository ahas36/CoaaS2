import { Component, OnInit } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-csms',
  templateUrl: './csms.component.html',
  styleUrls: ['./csms.component.css']
})
export class CSMSComponent implements OnInit {

  pcrcount

  constructor(private serviceAPI: ApiServiceService) {
    this.serviceAPI.getCSMSPerformanceData().subscribe(
      data => {
        this.pcrcount = data.csms
      }
    )
  }

  ngOnInit() {}
}



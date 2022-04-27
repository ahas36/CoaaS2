import { Component, OnInit } from '@angular/core';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-overall',
  templateUrl: './overall.component.html',
  styleUrls: ['./overall.component.css']
})
export class OverallComponent implements OnInit {

  gain;
  avg_gain;

  earning;
  penalty_cost;
  retrieval_cost;

  costearningratio;

  no_of_queries;
  no_of_retrievals;
  avg_query_overhead;
  avg_network_overhead;
  avg_processing_overhead;
  processing_overhead_ratio;
  network_overhead_ratio;

  query_latency;

  timeTicks;

  isGreater = false;

  constructor(private serviceAPI: ApiServiceService) {
    this.serviceAPI.getPerformanceSummary().subscribe(
      data => {
        // Top bar
        this.gain = data.gain.get();
        this.no_of_queries = data.no_of_queries.get();
        this.no_of_retrievals = data.no_of_retrievals.get();
        // + current average query latency

        // 2nd
        this.avg_gain = data.avg_gain.get();
        // + current gain

        // 3rd - Left
        this.earning = data.earning.get();
        this.penalty_cost = data.penalty_cost.get();
        this.retrieval_cost = data.retrieval_cost.get();

        // 3rd - Right
        // + current earning
        // + current penalty
        // + current retriveal cost 
        this.costearningratio = data.costearningratio.get();

        // 4th 
        this.avg_query_overhead = data.avg_query_overhead.get();
        this.avg_network_overhead = data.avg_network_overhead.get();
        this.avg_processing_overhead = data.avg_processing_overhead.get();
        this.processing_overhead_ratio = data.processing_overhead_ratio.get();
        this.network_overhead_ratio = data.network_overhead_ratio.get();

        // General
        this.timeTicks = data.timeTicks.get();
      });
    
      console.log(this.network_overhead_ratio);
  }

  ngOnInit() {}

}

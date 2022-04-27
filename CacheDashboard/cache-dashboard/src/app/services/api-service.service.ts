import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfData, Queue } from './service-classes';
import { SummaryModel } from './service-view-models';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ApiServiceService {

  csmsData;
  levelsData;
  summaryData = new SummaryModel();

  counter = 0;
  timeTicks:Queue<number> = new Queue<number>();

  constructor(private http:HttpClient) { }

  retrievePerformanceData(){
    this.counter += 1;
    this.timeTicks.push(this.counter);

    let apiData = this.http.get<PerfData>('https://5ca94f11-0239-495f-a486-ac28ef7f0d42.mock.pstmn.io/log/performance')

    apiData.subscribe((res) => {
      // Overall perfromance summary
      this.summaryData.avg_gain.push(res.summary.avg_gain);
      this.summaryData.gain.push(res.summary.gain);
      this.summaryData.earning.push(res.summary.earning);
      this.summaryData.penalty_cost.push(res.summary.penalty_cost);
      this.summaryData.retrieval_cost.push(res.summary.retrieval_cost);

      let ratio = res.summary.earning/(res.summary.penalty_cost + res.summary.retrieval_cost);
      this.summaryData.costearningratio.push(ratio);

      this.summaryData.no_of_queries.push(res.summary.no_of_queries);
      this.summaryData.no_of_retrievals.push(res.summary.no_of_retrievals);
      this.summaryData.avg_query_overhead.push(res.summary.avg_query_overhead);
      this.summaryData.avg_network_overhead.push(res.summary.avg_network_overhead);
      this.summaryData.avg_processing_overhead.push(res.summary.avg_processing_overhead);
      
      let oh_1_ratio = res.summary.avg_processing_overhead/res.summary.avg_query_overhead;
      this.summaryData.processing_overhead_ratio.push(oh_1_ratio) ;

      let oh_2_ratio = res.summary.avg_network_overhead/res.summary.avg_query_overhead;
      this.summaryData.network_overhead_ratio.push(oh_2_ratio);

      this.summaryData.timeTicks.push(this.counter);

      // CSMS Statistics
      this.csmsData = res.csms;

      // Cache level-wise statistics
      this.levelsData = res.levels;

    });
  }

  getPerformanceSummary(): Observable<SummaryModel>{
    return of(this.summaryData);
  }

  getCSMSPerformanceData() {
    return this.csmsData;
  }

  getCacheLevelPerformanceData(){
    return this.levelsData;
  }

}

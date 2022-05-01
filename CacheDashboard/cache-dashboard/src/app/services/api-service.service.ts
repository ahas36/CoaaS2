import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfData, Queue } from './service-classes';
import { SummaryModel } from './service-view-models';

@Injectable({
  providedIn: 'root'
})

export class ApiServiceService {

  apiData;
  csmsData;
  levelsData;
  summaryData;
  currentCosts;

  newDataReady = false;

  counter = 0;
  timeTicks:Queue<number> = new Queue<number>();

  constructor(private http:HttpClient) {
    this.summaryData = new SummaryModel();
    // this.triggerRefreshing();
  }

  triggerRefreshing(){
    while(true){
      setTimeout(() => this.retrievePerformanceData(), 60*1000);
    }
  }

  retrievePerformanceData(){
    this.counter += 1;
    this.timeTicks.push(this.counter);
    this.apiData = this.http.get<PerfData>('https://8d262b69-786f-4961-96d8-a7419d5dfe4b.mock.pstmn.io/log/performance');
    this.newDataReady = true;
  }

  getPerformanceSummary() {
    if(this.newDataReady || this.summaryData == undefined){
      this.apiData.subscribe(res => {
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
  
        this.summaryData.currentCosts.push(res.summary.earning);
        this.summaryData.currentCosts.push(res.summary.retrieval_cost);
        this.summaryData.currentCosts.push(res.summary.penalty_cost);
  
        let pod = res.summary.delayed_queries/res.summary.no_of_queries;
  
        this.summaryData.rt_pod.push({'x': res.summary.avg_query_overhead, 'y': pod});
        this.summaryData.noh_pod.push({'x': res.summary.avg_network_overhead, 'y': pod});
        this.summaryData.poh_pod.push({'x': res.summary.avg_processing_overhead, 'y': pod});
      });
    }

    this.newDataReady = false;
    return this.summaryData;
  }

  getCSMSPerformanceData() {
    if(this.newDataReady || this.csmsData == undefined){
      this.apiData.subscribe(res => {
        // CSMS Statistics
        this.csmsData = res.csms;
      });
    }
    
    this.newDataReady = false;
    return this.csmsData;
  }

  getCacheLevelPerformanceData(){
    if(this.newDataReady || this.levelsData == undefined){
      this.apiData.subscribe(res => {
        // Cache level-wise statistics
        this.levelsData = res.levels;
      });
    }
    
    this.newDataReady = false;
    return this.levelsData;
  }

}

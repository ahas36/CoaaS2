import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfData, Queue } from './service-classes';
import { CSMSModel, SummaryModel } from './service-view-models';

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
  summaryInit = false;
  levelsInit = false;
  csmsInit = false;

  counter = 0;
  timeTicks:Queue<number> = new Queue<number>();

  constructor(private http:HttpClient) {
    this.summaryData = new SummaryModel();
    this.csmsData = new CSMSModel();

    this.summaryInit = true;
    this.csmsInit = true;

    // levelsInit = false;
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
    if(this.newDataReady || this.summaryInit){
      this.apiData.subscribe(res => {
        // Overall perfromance summary
        this.summaryData.avg_gain.push(res.summary.avg_gain);
        this.summaryData.gain.push(res.summary.gain);
        this.summaryData.earning.push(res.summary.earning);
        this.summaryData.penalty_cost.push(res.summary.penalty_cost);
        this.summaryData.retrieval_cost.push(res.summary.retrieval_cost);
  
        let ratio = (res.summary.earning/(res.summary.penalty_cost + res.summary.retrieval_cost)).toFixed(2);
        this.summaryData.costearningratio.push(ratio);
  
        this.summaryData.no_of_queries.push(res.summary.no_of_queries);
        this.summaryData.no_of_retrievals.push(res.summary.no_of_retrievals);
        this.summaryData.avg_query_overhead.push(res.summary.avg_query_overhead);
        this.summaryData.avg_network_overhead.push(res.summary.avg_network_overhead);
        this.summaryData.avg_processing_overhead.push(res.summary.avg_processing_overhead);
        
        let oh_1_ratio = (res.summary.avg_processing_overhead/res.summary.avg_query_overhead).toFixed(2);
        this.summaryData.processing_overhead_ratio.push(oh_1_ratio) ;
  
        let oh_2_ratio = (res.summary.avg_network_overhead/res.summary.avg_query_overhead).toFixed(2);
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
    this.summaryInit = false;
    return this.summaryData;
  }

  getCSMSPerformanceData() {
    if(this.newDataReady || this.csmsInit){
      this.apiData.subscribe(res => {
        // HCR
        this.csmsData.hcr_ok.push(res.csms.handleContextRequest.ok.count);
        let total = res.csms.handleContextRequest.ok.count;
        if(res.csms.handleContextRequest.error != undefined){
          total += res.csms.handleContextRequest.error.count;
        }
        if(res.csms.handleContextRequest.unauth != undefined){
          total += res.csms.handleContextRequest.unauth.count;
        }
        if(res.csms.handleContextRequest.notfound != undefined){
          total += res.csms.handleContextRequest.notfound.count;
        }
        this.csmsData.hcr_all.push(total);
        console.log(total);
        this.csmsData.hcr_avg.push((res.csms.handleContextRequest.ok.average).toFixed(2));
        this.csmsData.hcr_sucess.push((res.csms.handleContextRequest.ok.count*100/total).toFixed(2));
        this.csmsData.cur_hcr_break.push(res.csms.handleContextRequest.ok.count);
        this.csmsData.cur_hcr_break.push(total-res.csms.handleContextRequest.ok.count);

        // DMS
        this.csmsData.dms_ok.push(res.csms.discoverMatchingService.ok.count);
        let dmstotal = res.csms.discoverMatchingService.ok.count;
        if(res.csms.discoverMatchingService.error != undefined){
          dmstotal += res.csms.discoverMatchingService.error.count
        }
        if(res.csms.discoverMatchingService.unauth != undefined){
          dmstotal += res.csms.discoverMatchingService.unauth.count
        }
        if(res.csms.discoverMatchingService.notfound != undefined){
          dmstotal += res.csms.discoverMatchingService.notfound.count
        }
        this.csmsData.dms_all.push(dmstotal);
        this.csmsData.dms_avg.push((res.csms.discoverMatchingService.ok.average).toFixed(2));
        this.csmsData.dms_sucess.push((res.csms.discoverMatchingService.ok.count*100/dmstotal).toFixed(2));
        this.csmsData.cur_dms_break.push(res.csms.discoverMatchingService.ok.count);
        this.csmsData.cur_dms_break.push(dmstotal-res.csms.discoverMatchingService.ok.count);

        // RCE
        this.csmsData.rce_ok.push(res.csms.handleContextRequest.ok.count);
        let rcetotal = res.csms.handleContextRequest.ok.count;
        if(res.csms.refreshContextEntity.error != undefined){
          rcetotal += res.csms.refreshContextEntity.error.count
        }
        if(res.csms.refreshContextEntity.unauth != undefined){
          rcetotal += res.csms.refreshContextEntity.unauth.count
        }
        if(res.csms.refreshContextEntity.notfound != undefined){
          rcetotal += res.csms.refreshContextEntity.notfound.count
        }
        this.csmsData.rce_all.push(rcetotal);
        this.csmsData.rce_avg.push((res.csms.refreshContextEntity.ok.average).toFixed(2));
        this.csmsData.rce_sucess.push((res.csms.handleContextRequest.ok.count*100/rcetotal).toFixed(2));
        this.csmsData.cur_rce_break.push(res.csms.handleContextRequest.ok.count);
        this.csmsData.cur_rce_break.push(rcetotal-res.csms.handleContextRequest.ok.count);

      });
    }
    
    this.newDataReady = false;
    this.csmsInit = false;
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
    this.levelsInit = false;
    return this.levelsData;
  }

  round(num, decimalPlaces = 2) {
    return Number(num + "e" + -decimalPlaces);
  }

}

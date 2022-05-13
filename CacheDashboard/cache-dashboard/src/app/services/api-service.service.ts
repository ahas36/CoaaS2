import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfData, Queue } from './service-classes';
import { CSMSModel, LevelsModel, SummaryModel } from './service-view-models';
import { config } from '../config';

@Injectable({
  providedIn: 'root'
})

export class ApiServiceService {

  apiData;
  csmsData;
  levelsData;
  summaryData;
  currentCosts;

  summaryInit = false;
  levelsInit = false;
  csmsInit = false;

  summaryV;
  levelsV;
  csmsV;

  carParkData;
  placesData;
  
  counter = 0;
  timeTicks:Queue<number> = new Queue<number>();

  constructor(private http:HttpClient) {
    this.summaryData = new SummaryModel();
    this.csmsData = new CSMSModel();
    this.levelsData = new LevelsModel();

    this.summaryInit = true;
    this.levelsInit = true;
    this.csmsInit = true;
  }

  retrievePerformanceData(){
    this.apiData = this.http.get<PerfData>(config.uri);
    this.counter += 1;
    this.timeTicks.push(this.counter);
  }

  getPerformanceSummary() {
    if(this.counter != this.summaryV || this.summaryInit){
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

        this.summaryData.currentCosts.splice(0, this.summaryData.currentCosts.length);
        this.summaryData.currentCosts.push(res.summary.earning);
        this.summaryData.currentCosts.push(res.summary.retrieval_cost);
        this.summaryData.currentCosts.push(res.summary.penalty_cost);
  
        let pod = res.summary.delayed_queries/res.summary.no_of_queries;
  
        this.summaryData.rt_pod.push({'x': res.summary.avg_query_overhead, 'y': pod});
        this.summaryData.noh_pod.push({'x': res.summary.avg_network_overhead, 'y': pod});
        this.summaryData.poh_pod.push({'x': res.summary.avg_processing_overhead, 'y': pod});
      });
    }

    this.summaryV = this.counter;
    this.summaryInit = false;
    return this.summaryData;
  }

  getCSMSPerformanceData() {
    if(this.counter != this.csmsV || this.csmsInit){
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
        this.csmsData.hcr_avg.push((res.csms.handleContextRequest.ok.average).toFixed(2));
        this.csmsData.hcr_sucess.push((res.csms.handleContextRequest.ok.count*100/total).toFixed(2));

        this.csmsData.cur_hcr_break.splice(0, this.csmsData.cur_hcr_break.length);
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

        this.csmsData.cur_dms_break.splice(0, this.csmsData.cur_dms_break.length);
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

        this.csmsData.cur_rce_break.splice(0, this.csmsData.cur_rce_break.length);
        this.csmsData.cur_rce_break.push(res.csms.handleContextRequest.ok.count);
        this.csmsData.cur_rce_break.push(rcetotal-res.csms.handleContextRequest.ok.count);

        this.csmsData.timeTicks.push(this.counter);

      });
    }
    
    this.csmsV = this.counter;
    this.csmsInit = false;
    return this.csmsData;
  }

  getCacheLevelPerformanceData(){
    if(this.counter != this.levelsV || this.levelsInit){
      this.apiData.subscribe(res => {
        // Cache level-wise statistics
        this.levelsData.entity_hr.push(res.levels.entity.hitrate);
        this.levelsData.entity_hit_rt.push(res.levels.entity.hit_response_time);
        this.levelsData.entity_miss_rt.push(res.levels.entity.miss_response_time);
        this.levelsData.entity_cached.push(res.levels.entity.items.length);
        this.levelsData.entity_items = res.levels.entity.items;
        this.levelsData.entity_hits_hr = [];
        this.levelsData.entity_items_hits = [];
        this.levelsData.entity_items_miss = [];
        this.levelsData.entity_items_hr = [];
        this.levelsData.entity_nos = res.levels.entity.items.length;

        for(let element of res.levels.entity.items){
          this.levelsData.entity_hits_hr.push({'x': element.hitrate, 'y': element.hits});
          this.levelsData.entity_items_hits.push(element.hits);
          this.levelsData.entity_items_miss.push(element.misses);
          this.levelsData.entity_items_hr.push(element.hitrate);
        }

        this.levelsData.situfunc_hr.push(res.levels.situfunction.hitrate);
        this.levelsData.situfunc_hit_rt.push(res.levels.situfunction.hit_response_time);
        this.levelsData.situfunc_miss_rt.push(res.levels.situfunction.miss_response_time);
        this.levelsData.situfunc_cached.push(res.levels.situfunction.items.length);
        this.levelsData.situfunc_items = res.levels.situfunction.items;
        this.levelsData.situfunc_hits_hr = [];
        this.levelsData.situfunc_items_hits = [];
        this.levelsData.situfunc_items_miss = [];
        this.levelsData.situfunc_items_hr = [];
        this.levelsData.situfunc_nos = res.levels.situfunction.items.length;

        for(let element of res.levels.situfunction.items){
          this.levelsData.situfunc_hits_hr.push({'x': element.hitrate, 'y': element.hits});
          this.levelsData.situfunc_items_hits.push(element.hits);
          this.levelsData.situfunc_items_miss.push(element.misses);
          this.levelsData.situfunc_items_hr.push(element.hitrate);
        }

        this.levelsData.aggfunc_hr.push(res.levels.aggfunction.hitrate);
        this.levelsData.aggfunc_hit_rt.push(res.levels.aggfunction.hit_response_time);
        this.levelsData.aggfunc_miss_rt.push(res.levels.aggfunction.miss_response_time);
        this.levelsData.aggfunc_cached.push(res.levels.aggfunction.items.length);
        this.levelsData.aggfunc_items = res.levels.aggfunction.items;
        this.levelsData.aggfunc_hits_hr = [];
        this.levelsData.aggfunc_items_hits = [];
        this.levelsData.aggfunc_items_miss = [];
        this.levelsData.aggfunc_items_hr = [];
        this.levelsData.aggfunc_nos = res.levels.aggfunction.items.length;

        for(let element of res.levels.aggfunction.items){
          this.levelsData.aggfunc_hits_hr.push({'x': element.hitrate, 'y': element.hits});
          this.levelsData.aggfunc_items_hits.push(element.hits);
          this.levelsData.aggfunc_items_miss.push(element.misses);
          this.levelsData.aggfunc_items_hr.push(element.hitrate);
        }

        this.levelsData.cr_hr.push(res.levels.contextrequest.hitrate);
        this.levelsData.cr_hit_rt.push(res.levels.contextrequest.hit_response_time);
        this.levelsData.cr_miss_rt.push(res.levels.contextrequest.miss_response_time);
        this.levelsData.cr_cached.push(res.levels.contextrequest.items.length);
        this.levelsData.cr_items = res.levels.contextrequest.items;
        this.levelsData.cr_hits_hr = [];
        this.levelsData.cr_items_hits = [];
        this.levelsData.cr_items_miss = [];
        this.levelsData.cr_items_hr = [];
        this.levelsData.cr_nos = res.levels.contextrequest.items.length;

        for(let element of res.levels.contextrequest.items){
          this.levelsData.cr_hits_hr.push({'x': element.hitrate, 'y': element.hits});
          this.levelsData.cr_items_hits.push(element.hits);
          this.levelsData.cr_items_miss.push(element.misses);
          this.levelsData.cr_items_hr.push(element.hitrate);
        }

        this.levelsData.q_hr.push(res.levels.query.hitrate);
        this.levelsData.q_hit_rt.push(res.levels.query.hit_response_time);
        this.levelsData.q_miss_rt.push(res.levels.query.miss_response_time);
        this.levelsData.q_cached.push(res.levels.query.items.length);
        this.levelsData.q_items = res.levels.query.items;
        this.levelsData.q_hits_hr = [];
        this.levelsData.q_items_hits = [];
        this.levelsData.q_items_miss = [];
        this.levelsData.q_items_hr = [];
        this.levelsData.q_nos = res.levels.query.items.length;

        for(let element of res.levels.query.items){
          this.levelsData.q_hits_hr.push({'x': element.hitrate, 'y': element.hits});
          this.levelsData.q_items_hits.push(element.hits);
          this.levelsData.q_items_miss.push(element.misses);
          this.levelsData.q_items_hr.push(element.hitrate);
        }

        this.levelsData.timeTicks.push(this.counter);
      });
    }
    
    this.levelsV = this.counter;
    this.levelsInit = false;
    return this.levelsData;
  }

  getCarParks(){
    if(this.carParkData == undefined){
      this.carParkData = this.http.get(config.carparks);
    }
    console.log(this.carParkData);
    return this.carParkData;
  }

  getPlaces(){
    if(this.placesData == undefined){
      this.placesData = this.http.get(config.places);
    }
    return this.placesData;
  }

}

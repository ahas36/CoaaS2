import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PerfData, QueryStats, ModelState, Queue } from './service-classes';
import { CSMSModel, LevelsModel, SummaryModel, SimpleModel, LearningModel } from './service-view-models';
import { config } from '../config';

const divisor = 1073741824;

@Injectable({
  providedIn: 'root'
})

export class ApiServiceService {
  apiData;
  queryLocs;
  modelState;

  csmsData;
  levelsData;
  summaryData;
  currentCosts;
  queryData;
  modelData;

  summaryInit = false;
  levelsInit = false;
  csmsInit = false;
  queryInit = false;
  modelInit = false;

  summaryV;
  levelsV;
  csmsV;
  queryV;
  modelV;

  carParkData;
  placesData;

  counter = 0;
  
  timeTicks:Queue<number> = new Queue<number>();

  headers = new HttpHeaders()
    .set('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');

  constructor(private http:HttpClient) {
    this.summaryData = new SummaryModel();
    this.csmsData = new CSMSModel();
    this.levelsData = new LevelsModel();
    this.queryData = new SimpleModel();
    this.modelData = new LearningModel();

    this.summaryInit = true;
    this.levelsInit = true;
    this.csmsInit = true;
    this.queryInit = true;
    this.modelInit = true;
  }

  retrievePerformanceData(){
    this.apiData = this.http.get<PerfData>(config.uri, {'headers':this.headers});
    this.queryLocs = this.http.get<QueryStats>(config.querystaturi, {'headers':this.headers});
    this.modelState = this.http.get<ModelState>(config.model_uri, {'headers':this.headers});
    this.counter += 1;
    this.timeTicks.push(this.counter);
  }

  getQueryLoadVariation (){
    if(this.counter != this.queryV || this.queryInit){
      this.apiData.subscribe(res => {
        this.queryData.query_load.push(parseInt(res.summary.no_of_queries.$numberLong));
        this.queryData.timeTicks.push(this.counter);
      });
    }

    this.queryV = this.counter;
    this.queryInit = false;

    return {
      "perf": this.queryData,
      "query": this.queryLocs.queries
    }
  }

  getModelVariation (){
    if(this.counter != this.modelV || this.modelInit){
      this.modelState.subscribe(res => {
        this.modelData.threshold.push(res.threshold.toFixed(2));
        this.modelData.kappa.push(res.kappa.toFixed(2));
        this.modelData.mu.push(res.mu.toFixed(2));
        this.modelData.pi.push(res.pi.toFixed(2));
        this.modelData.delta.push(res.delta.toFixed(2));
        this.modelData.row.push(res.row.toFixed(2));
        this.modelData.avg_cachelife.push(res.avg_cachelife);
        this.modelData.avg_delaytime.push(res.avg_delaytime);
        this.modelData.avg_reward.push(res.avg_reward.toFixed(2));
        this.modelData.timeTicks.push(this.counter);

        this.modelData.reward_th.push({'x':res.threshold.toFixed(2), 'y':res.avg_reward.toFixed(2)});
        this.modelData.cl_th.push({'x':res.threshold.toFixed(2), 'y': res.avg_cachelife});
        this.modelData.dt_th.push({'x':res.threshold.toFixed(2), 'y': res.avg_delaytime});
      });
    }

    this.modelV = this.counter;
    this.modelInit = false;

    return this.modelData
  }

  getPerformanceSummary() {
    if(this.counter != this.summaryV || this.summaryInit){
      this.apiData.subscribe(res => {
        // Overall perfromance summary
        this.summaryData.avg_gain.push(res.summary.avg_gain.toFixed(2));
        this.summaryData.gain.push(res.summary.gain.toFixed(2));
        this.summaryData.avg_total_gain.push(res.summary.avg_total_gain.toFixed(2));
        this.summaryData.total_gain.push(res.summary.total_gain.toFixed(2));

        this.summaryData.earning.push(res.summary.earning.toFixed(2));
        this.summaryData.penalty_earning.push(res.summary.penalty_earning.toFixed(2));
        this.summaryData.penalty_cost.push(res.summary.penalty_cost.toFixed(2));
        this.summaryData.retrieval_cost.push(res.summary.retrieval_cost.toFixed(2));
        this.summaryData.cache_cost.push(res.summary.cache_cost.toFixed(2));
        this.summaryData.processing_cost.push(res.summary.processing_cost.toFixed(2));

        let ratio = (res.summary.earning/(res.summary.penalty_cost + res.summary.retrieval_cost 
          + res.summary.cache_cost + res.summary.processing_cost)).toFixed(2);
        this.summaryData.costearningratio.push(ratio);

        this.summaryData.no_of_queries.push(parseInt(res.summary.no_of_queries.$numberLong));
        this.summaryData.no_of_retrievals.push(parseInt(res.summary.no_of_retrievals.$numberLong));
        this.summaryData.avg_query_overhead.push((parseFloat(res.summary.avg_query_overhead.$numberLong)/1000).toFixed(2));
        this.summaryData.avg_network_overhead.push(parseFloat(res.summary.avg_network_overhead.$numberLong).toFixed(2));
        this.summaryData.avg_processing_overhead.push(res.summary.avg_processing_overhead.toFixed(2));

        let oh_1_ratio = (res.summary.avg_processing_overhead/parseFloat(res.summary.avg_query_overhead.$numberLong)).toFixed(2);
        this.summaryData.processing_overhead_ratio.push(oh_1_ratio) ;

        let oh_2_ratio = (parseFloat(res.summary.avg_network_overhead.$numberLong)/parseFloat(res.summary.avg_query_overhead.$numberLong)).toFixed(2);
        this.summaryData.network_overhead_ratio.push(oh_2_ratio);

        this.summaryData.timeTicks.push(this.counter);

        this.summaryData.currentCosts.splice(0, this.summaryData.currentCosts.length);
        this.summaryData.currentCosts.push(res.summary.earning.toFixed(2));
        this.summaryData.currentCosts.push(res.summary.penalty_earning.toFixed(2));
        this.summaryData.currentCosts.push(res.summary.retrieval_cost.toFixed(2));
        this.summaryData.currentCosts.push(res.summary.penalty_cost.toFixed(2));
        this.summaryData.currentCosts.push(res.summary.processing_cost.toFixed(2));
        this.summaryData.currentCosts.push(res.summary.cache_cost.toFixed(2));

        let pod = (parseFloat(res.summary.delayed_queries.$numberLong)/parseInt(res.summary.no_of_queries.$numberLong)).toFixed(2);

        this.summaryData.rt_pod.push({'x': parseFloat(res.summary.avg_query_overhead.$numberLong).toFixed(2), 'y': pod});
        this.summaryData.noh_pod.push({'x': parseFloat(res.summary.avg_network_overhead.$numberLong).toFixed(2), 'y': pod});
        this.summaryData.poh_pod.push({'x': res.summary.avg_processing_overhead.toFixed(2), 'y': pod});

        let cachestats = res.cachememory;
        this.summaryData.cache.used_memory = (cachestats.used_memory.value/divisor).toFixed(2);
        this.summaryData.cache.used_memory_dataset = (cachestats.used_memory_dataset.value/divisor).toFixed(2);
        this.summaryData.cache.total_system_memory = (cachestats.total_system_memory.value/divisor).toFixed(2);
        this.summaryData.cache.used_memory_dataset_perc = cachestats.used_memory_dataset_perc.value.toFixed(2);
        this.summaryData.cache.used_memory_peak_perc = cachestats.used_memory_peak_perc.value.toFixed(2);

        this.summaryData.total_cache.push(this.summaryData.cache.total_system_memory);
        this.summaryData.occupied_cache.push(this.summaryData.cache.used_memory);
        this.summaryData.data_occupied_cache.push(this.summaryData.cache.used_memory_dataset);

        let overhead = this.summaryData.cache.used_memory - this.summaryData.cache.used_memory_dataset
        let unoccupied = this.summaryData.cache.total_system_memory - this.summaryData.cache.used_memory

        var unoccu_perc = unoccupied/this.summaryData.cache.total_system_memory;
        var oh_perc = overhead/this.summaryData.cache.total_system_memory;
        var data_perc = 1 - oh_perc - unoccu_perc;

        this.summaryData.cache_occupancy.splice(0, this.summaryData.cache_occupancy.length);
        this.summaryData.cache_occupancy.push(unoccu_perc.toFixed(2));
        this.summaryData.cache_occupancy.push(data_perc.toFixed(2));
        this.summaryData.cache_occupancy.push(oh_perc.toFixed(2));

        this.summaryData.exp_earn.push(res.expectedSLA.exp_earn.toFixed(2));
        this.summaryData.exp_fth.push(res.expectedSLA.exp_fth.toFixed(2));
        this.summaryData.exp_pen.push(res.expectedSLA.exp_pen.toFixed(2));
        this.summaryData.exp_rtmax.push((res.expectedSLA.exp_rtmax/1000).toFixed(2));

        this.summaryData.con_age.push(res.avgContextAge.toFixed(2));
      });
    }

    this.summaryV = this.counter;
    this.summaryInit = false;
    return this.summaryData;
  }

  getCSMSPerformanceData() {
    if(this.counter != this.csmsV || this.csmsInit){
      this.apiData.subscribe(res => {
        // DMS
        if(res.csms.discoverMatchingServices != undefined){
          let dmstotal = 0;
          if(res.csms.discoverMatchingServices.ok != undefined){
            this.csmsData.dms_ok.push(res.csms.discoverMatchingServices.ok.count);
            dmstotal += res.csms.discoverMatchingServices.ok.count;
          }
          if(res.csms.discoverMatchingServices.error != undefined){
            dmstotal += res.csms.discoverMatchingServices.error.count
          }
          if(res.csms.discoverMatchingServices.unauth != undefined){
            dmstotal += res.csms.discoverMatchingServices.unauth.count
          }
          if(res.csms.discoverMatchingServices.notfound != undefined){
            dmstotal += res.csms.discoverMatchingService.notfound.count
          }
          this.csmsData.dms_all.push(dmstotal);
          this.csmsData.dms_avg.push((res.csms.discoverMatchingServices.ok.average).toFixed(2));
          this.csmsData.dms_sucess.push((res.csms.discoverMatchingServices.ok.count*100/dmstotal).toFixed(2));

          this.csmsData.cur_dms_break.splice(0, this.csmsData.cur_dms_break.length);
          this.csmsData.cur_dms_break.push(res.csms.discoverMatchingServices.ok.count);
          this.csmsData.cur_dms_break.push(dmstotal - res.csms.discoverMatchingServices.ok.count);
        }

        // HCR
        if(res.csms.handleContextRequest != undefined) {
          let total = 0;
          if(res.csms.handleContextRequest.ok != undefined){
            this.csmsData.hcr_ok.push(res.csms.handleContextRequest.ok.count);
            total += res.csms.handleContextRequest.ok.count;
          }
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
          this.csmsData.cur_hcr_break.push(total - res.csms.handleContextRequest.ok.count);
        }
        
        // RCE
        if(res.csms.handleContextRequest != undefined){
          let rcetotal = 0;
          if(res.csms.handleContextRequest.ok != undefined){
            this.csmsData.rce_ok.push(res.csms.handleContextRequest.ok.count);
            rcetotal += res.csms.handleContextRequest.ok.count;
          }
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
        }
        
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
        this.levelsData.entity_hr.push(res.levels.entity.hitrate.toFixed(2));
        this.levelsData.entity_hit_rt.push(res.levels.entity.hit_response_time.toFixed(2));
        this.levelsData.entity_miss_rt.push(res.levels.entity.miss_response_time.toFixed(2));
        this.levelsData.entity_cached.push(res.levels.entity.items.length);
        this.levelsData.entity_items = res.levels.entity.items;
        this.levelsData.entity_hits_hr = [];
        this.levelsData.entity_items_hits = [];
        this.levelsData.entity_items_miss = [];
        this.levelsData.entity_items_hr = [];
        this.levelsData.entity_nos = res.levels.entity.items.length;

        for(let element of res.levels.entity.items){
          this.levelsData.entity_hits_hr.push({'x': element.hitrate, 'y': element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0});
          this.levelsData.entity_items_hits.push(element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0);
          this.levelsData.entity_items_miss.push(element.misses != undefined ? parseInt(element.misses.$numberLong) : 0.0);
          this.levelsData.entity_items_hr.push(element.hitrate);
        }

        this.levelsData.situfunc_hr.push(res.levels.situfunction.hitrate.toFixed(2));
        this.levelsData.situfunc_hit_rt.push(res.levels.situfunction.hit_response_time.toFixed(2));
        this.levelsData.situfunc_miss_rt.push(res.levels.situfunction.miss_response_time.toFixed(2));
        this.levelsData.situfunc_cached.push(res.levels.situfunction.items.length);
        this.levelsData.situfunc_items = res.levels.situfunction.items;
        this.levelsData.situfunc_hits_hr = [];
        this.levelsData.situfunc_items_hits = [];
        this.levelsData.situfunc_items_miss = [];
        this.levelsData.situfunc_items_hr = [];
        this.levelsData.situfunc_nos = res.levels.situfunction.items.length;

        for(let element of res.levels.situfunction.items){
          this.levelsData.situfunc_hits_hr.push({'x': element.hitrate, 'y': element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0});
          this.levelsData.situfunc_items_hits.push(element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0);
          this.levelsData.situfunc_items_miss.push(element.misses != undefined ? parseInt(element.misses.$numberLong) : 0.0);
          this.levelsData.situfunc_items_hr.push(element.hitrate);
        }

        this.levelsData.aggfunc_hr.push(res.levels.aggfunction.hitrate.toFixed(2));
        this.levelsData.aggfunc_hit_rt.push(res.levels.aggfunction.hit_response_time.toFixed(2));
        this.levelsData.aggfunc_miss_rt.push(res.levels.aggfunction.miss_response_time.toFixed(2));
        this.levelsData.aggfunc_cached.push(res.levels.aggfunction.items.length);
        this.levelsData.aggfunc_items = res.levels.aggfunction.items;
        this.levelsData.aggfunc_hits_hr = [];
        this.levelsData.aggfunc_items_hits = [];
        this.levelsData.aggfunc_items_miss = [];
        this.levelsData.aggfunc_items_hr = [];
        this.levelsData.aggfunc_nos = res.levels.aggfunction.items.length;

        for(let element of res.levels.aggfunction.items){
          this.levelsData.aggfunc_hits_hr.push({'x': element.hitrate, 'y': element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0});
          this.levelsData.aggfunc_items_hits.push(element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0);
          this.levelsData.aggfunc_items_miss.push(element.misses != undefined ? parseInt(element.misses.$numberLong) : 0.0);
          this.levelsData.aggfunc_items_hr.push(element.hitrate);
        }

        this.levelsData.cr_hr.push(res.levels.contextrequest.hitrate.toFixed(2));
        this.levelsData.cr_hit_rt.push(res.levels.contextrequest.hit_response_time.toFixed(2));
        this.levelsData.cr_miss_rt.push(res.levels.contextrequest.miss_response_time.toFixed(2));
        this.levelsData.cr_cached.push(res.levels.contextrequest.items.length);
        this.levelsData.cr_items = res.levels.contextrequest.items;
        this.levelsData.cr_hits_hr = [];
        this.levelsData.cr_items_hits = [];
        this.levelsData.cr_items_miss = [];
        this.levelsData.cr_items_hr = [];
        this.levelsData.cr_nos = res.levels.contextrequest.items.length;

        for(let element of res.levels.contextrequest.items){
          this.levelsData.cr_hits_hr.push({'x': element.hitrate, 'y': element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0});
          this.levelsData.cr_items_hits.push(element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0);
          this.levelsData.cr_items_miss.push(element.misses != undefined ? parseInt(element.misses.$numberLong) : 0.0);
          this.levelsData.cr_items_hr.push(element.hitrate);
        }

        this.levelsData.q_hr.push(res.levels.query.hitrate.toFixed(2));
        this.levelsData.q_hit_rt.push(res.levels.query.hit_response_time.toFixed(2));
        this.levelsData.q_miss_rt.push(res.levels.query.miss_response_time.toFixed(2));
        this.levelsData.q_cached.push(res.levels.query.items.length);
        this.levelsData.q_items = res.levels.query.items;
        this.levelsData.q_hits_hr = [];
        this.levelsData.q_items_hits = [];
        this.levelsData.q_items_miss = [];
        this.levelsData.q_items_hr = [];
        this.levelsData.q_nos = res.levels.query.items.length;

        for(let element of res.levels.query.items){
          this.levelsData.q_hits_hr.push({'x': element.hitrate, 'y': element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0});
          this.levelsData.q_items_hits.push(element.hits != undefined ? parseInt(element.hits.$numberLong) : 0.0);
          this.levelsData.q_items_miss.push(element.misses != undefined ? parseInt(element.misses.$numberLong) : 0.0);
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

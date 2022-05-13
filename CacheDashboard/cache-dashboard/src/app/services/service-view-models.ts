import { Injectable } from '@angular/core';
import { Queue } from './service-classes';

@Injectable()
export class SummaryModel{
  public gain: Queue<number>;
  public avg_gain: Queue<number>;
  
  public earning: Queue<number>;
  public penalty_cost: Queue<number>;
  public retrieval_cost: Queue<number>;
  public costearningratio: Queue<number>;

  public no_of_queries: Queue<number>;
  public no_of_retrievals: Queue<number>;
  public avg_query_overhead: Queue<number>;
  public avg_network_overhead: Queue<number>;
  public avg_processing_overhead: Queue<number>;
  public processing_overhead_ratio: Queue<number>;
  public network_overhead_ratio: Queue<number>;

  public rt_pod;
  public noh_pod;
  public poh_pod;

  public timeTicks:Queue<number>;
  public currentCosts;
    
  constructor(){
    this.gain = new Queue<number>();
    this.avg_gain = new Queue<number>();
  
    this.earning = new Queue<number>();
    this.penalty_cost = new Queue<number>();
    this.retrieval_cost = new Queue<number>();
    this.costearningratio = new Queue<number>();

    this.no_of_queries = new Queue<number>();
    this.no_of_retrievals = new Queue<number>();
    this.avg_query_overhead = new Queue<number>();
    this.avg_network_overhead = new Queue<number>();
    this.avg_processing_overhead = new Queue<number>();
    this.processing_overhead_ratio = new Queue<number>();
    this.network_overhead_ratio = new Queue<number>();

    this.rt_pod = [];
    this.noh_pod = [];
    this.poh_pod = [];

    this.timeTicks = new Queue<number>();
    this.currentCosts = [];
  }   
}

@Injectable()
export class CSMSModel{
  public hcr_ok: Queue<number>;
  public hcr_all: Queue<number>;
  public hcr_avg: Queue<number>;
  public hcr_sucess: Queue<number>;
  public cur_hcr_break;


  public dms_ok: Queue<number>; 
  public dms_all: Queue<number>;
  public dms_avg: Queue<number>;
  public dms_sucess: Queue<number>;
  public cur_dms_break;

  public rce_ok: Queue<number>;
  public rce_all: Queue<number>;
  public rce_avg: Queue<number>;
  public rce_sucess: Queue<number>;
  public cur_rce_break;

  public timeTicks: Queue<number>;
    
  constructor(){
    this.hcr_ok = new Queue<number>();
    this.hcr_all = new Queue<number>();
    this.hcr_avg = new Queue<number>();
    this.hcr_sucess = new Queue<number>();
    this.cur_hcr_break = [];
  
    this.dms_ok = new Queue<number>();
    this.dms_all = new Queue<number>();
    this.dms_avg = new Queue<number>();
    this.dms_sucess = new Queue<number>();
    this.cur_dms_break = [];

    this.rce_ok = new Queue<number>();
    this.rce_all = new Queue<number>();
    this.rce_avg = new Queue<number>();
    this.rce_sucess = new Queue<number>();
    this.cur_rce_break = [];

    this.timeTicks = new Queue<number>();
  }   
}

@Injectable()
export class LevelsModel{
  public entity_hr: Queue<number>;
  public entity_hit_rt: Queue<number>;
  public entity_miss_rt: Queue<number>;
  public entity_cached: Queue<number>;
  public entity_items;
  public entity_hits_hr;
  public entity_items_hits;
  public entity_items_miss;
  public entity_items_hr;
  public entity_nos;


  public situfunc_hr: Queue<number>;
  public situfunc_hit_rt: Queue<number>;
  public situfunc_miss_rt: Queue<number>;
  public situfunc_cached: Queue<number>;
  public situfunc_items;
  public situfunc_hits_hr;
  public situfunc_items_hits;
  public situfunc_items_miss;
  public situfunc_items_hr;
  public situfunc_nos;

  public aggfunc_hr: Queue<number>;
  public aggfunc_hit_rt: Queue<number>;
  public aggfunc_miss_rt: Queue<number>;
  public aggfunc_cached: Queue<number>;
  public aggfunc_items;
  public aggfunc_hits_hr;
  public aggfunc_items_hits;
  public aggfunc_items_miss;
  public aggfunc_items_hr;
  public aggfunc_nos;

  public cr_hr: Queue<number>;
  public cr_hit_rt: Queue<number>;
  public cr_miss_rt: Queue<number>;
  public cr_cached: Queue<number>;
  public cr_items;
  public cr_hits_hr;
  public cr_items_hits;
  public cr_items_miss;
  public cr_items_hr;
  public cr_nos;

  public q_hr: Queue<number>;
  public q_hit_rt: Queue<number>;
  public q_miss_rt: Queue<number>;
  public q_cached: Queue<number>;
  public q_items;
  public q_hits_hr;
  public q_items_hits;
  public q_items_miss;
  public q_items_hr;
  public q_nos;

  public timeTicks: Queue<number>;
    
  constructor(){
    this.entity_hr = new Queue<number>();
    this.entity_hit_rt = new Queue<number>();
    this.entity_miss_rt = new Queue<number>();
    this.entity_cached = new Queue<number>();

    this.situfunc_hr = new Queue<number>();
    this.situfunc_hit_rt = new Queue<number>();
    this.situfunc_miss_rt = new Queue<number>();
    this.situfunc_cached = new Queue<number>();

    this.aggfunc_hr = new Queue<number>();
    this.aggfunc_hit_rt = new Queue<number>();
    this.aggfunc_miss_rt = new Queue<number>();
    this.aggfunc_cached = new Queue<number>();

    this.cr_hr = new Queue<number>();
    this.cr_hit_rt = new Queue<number>();
    this.cr_miss_rt = new Queue<number>();
    this.cr_cached = new Queue<number>();

    this.q_hr = new Queue<number>();
    this.q_hit_rt = new Queue<number>();
    this.q_miss_rt = new Queue<number>();
    this.q_cached = new Queue<number>();

    this.timeTicks = new Queue<number>();
  }   
}

@Injectable()
export class SimpleModel{
  public query_load: Queue<number>;
  public timeTicks: Queue<number>;

  constructor(){
    this.query_load = new Queue<number>();
    this.timeTicks = new Queue<number>();
  }
}
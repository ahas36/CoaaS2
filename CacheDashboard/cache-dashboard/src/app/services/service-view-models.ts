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
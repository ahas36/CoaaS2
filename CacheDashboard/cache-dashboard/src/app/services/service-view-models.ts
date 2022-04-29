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

      this.timeTicks = new Queue<number>();
      this.currentCosts = [];
    }
    
  }
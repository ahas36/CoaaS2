import { Injectable } from '@angular/core';

@Injectable()
export class PerfData{
  public csms: CSMS;
  public coass: COASS;
  public levels: Level;
  public summary: Summary;
  public cachememory: CacheStat;
  public expectedSLA: ExpSLA;
}

export class ExpSLA{
  public exp_pen: number;
  public exp_fth: number;
  public exp_earn: number;
  public exp_rtmax: number;
}

export class CSMS{
  public handleContextRequest:Method;
  public discoverMatchingServices:Method;
  public refreshContextEntity:Method;
}

export class COASS{
  public execute:Method;
  public executeFetch:Method;
}

export class Level{
  public entity:LevelStat;
  public situfunction:LevelStat;
  public aggfunction:LevelStat;
  public contextrequest:LevelStat;
  public query:LevelStat;
}

export class CacheStat{
  public used_memory: CachePerfStat;
  public total_system_memory: CachePerfStat;
  public used_memory_dataset: CachePerfStat;
  public used_memory_peak_perc: CachePerfStat;
  public used_memory_dataset_perc: CachePerfStat;
}

export class CachePerfStat{
  public value: number;
  public unit: String;
}

export class LevelStat{
  public items:Item[];
  public hitrate:number;
  public hit_reponse_time:number;
  public miss_reponse_time:number;
}

export class Item{
  public id: string;
  public hits: longType;
  public misses: longType;
  public hitrate: number;
}

export class Summary{
  public gain: number;
  public avg_gain: number;
  public total_gain: number;
  public avg_total_gain: number;
  public no_of_queries: longType;
  public delayed_queries: longType;
  public no_of_retrievals: longType;
  public avg_query_overhead: longType;
  public avg_network_overhead: longType;
  public avg_processing_overhead: number;
  public earning:  number;
  public penalty_earning:  number;
  public cache_cost: number;
  public penalty_cost: number;
  public retrieval_cost: number;
  public processing_cost: number;
}

export class longType {
  public $numberLong : string;
}

export class Method {
  public ok:Status;
  public unauth:Status;
  public notfound:Status;
  public error:Status;
}

export class Status{
  public count: number;
  public average: number;
}

export class Queue<T> {
    private size = 60; // Queing hour worth of statistics
    _store: T[] = [];

    public push(val: T) {
        if(this._store.length >= this.size){
            this.pop();
        }

        this._store.push(val);
    }

    public pop(): T | undefined {
      return this._store.shift();
    }

    public get(){
        return this._store;
    }

    public getLast(){
      return this._store[this._store.length-1];
    }

    public notEmpty(){
      return this._store.length > 0;
    }

  }

  @Injectable()
  export class QueryStats {
    public queries: [QueryData];
  }
  
  export class QueryData{
    public location: Location;
    public address: String;
  }

  export class Location{
    public lat: number;
    public lng: number;
  }

  @Injectable()
  export class ModelState{
    public threshold: number;
    public kappa: number;
    public mu: number;
    public pi: number;
    public delta: number;
    public row: number;
    public avg_cachelife: number;
    public avg_delaytime: number;
    public avg_reward: number;
  }



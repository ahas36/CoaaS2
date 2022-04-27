import { Injectable } from '@angular/core';

@Injectable()
export class PerfData{
  public csms: CSMS;
  public coass: COASS;
  public levels: Level;
  public summary: Summary;
}

export class CSMS{
  public handleContextRequest:Method;
  public discoverMatchingService:Method;
  public refreshContextEntity:Method;
}

export class COASS{
  public execute:Method;
  public executeFetch:Method;
}

export class Level{
  public entity:Method;
  public situfunction:Method;
  public aggfunction:Method;
  public contextrequest:Method;
  public query:Method;
}

export class LevelStat{
  public items:Item[];
  public hitrate:number;
  public hit_reponse_time:number;
  public miss_reponse_time:number;
}

export class Item{
  public id: string;
  public hits: number;
  public misses: number;
  public hitrate: number;
}

export class Summary{
  public gain: number;
  public avg_gain: number;
  public no_of_queries: number;
  public no_of_retrievals: number;
  public avg_query_overhead: number;
  public avg_network_overhead: number;
  public avg_processing_overhead: number;
  public earning:  number;
  public penalty_cost: number;
  public retrieval_cost: number;
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
    private size = 10;
    private _store: T[] = [];
    
    public push(val: T) {
        if(this._store.length >= 10){
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

  }
import { Component, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip, SingleDataSet } from 'ng2-charts';
import { config } from '../config';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-cachelevels',
  templateUrl: './cachelevels.component.html',
  styleUrls: ['./cachelevels.component.css']
})
export class CacheLevelsComponent implements OnInit {

  timeTicks;

  entity_hr;
  entity_hit_rt;
  entity_miss_rt;
  entity_cached;

  situfunc_hr;
  situfunc_hit_rt;
  situfunc_miss_rt;
  situfunc_cached;

  aggfunc_hr;
  aggfunc_hit_rt;
  aggfunc_miss_rt;
  aggfunc_cached;

  cr_hr;
  cr_hit_rt;
  cr_miss_rt;
  cr_cached;

  q_hr;
  q_hit_rt;
  q_miss_rt;
  q_cached;

  public barChartType: ChartType = 'bar';
  public scatterChartType: ChartType = 'scatter';
  public lineChartType: ChartType = 'line';

  public chartPlugins = [];
  public entitybarChartLabels: Label[] = [];
  public situfuncbarChartLabels: Label[] = [];
  public aggfuncbarChartLabels: Label[] = [];
  public crbarChartLabels: Label[] = [];
  public qbarChartLabels: Label[] = [];

  public entityHRChartData: ChartDataSets[] = [];
  public entitySeekChartData: ChartDataSets[] = [];
  public entitynosChartData: ChartDataSets[] = [];
  public entityhitshrChartData: ChartDataSets[] = [];
  public entityhitsChartData: ChartDataSets[] = [];
  public entitymissesChartData: ChartDataSets[] = [];
  public entityhrChartData: ChartDataSets[] = [];

  public situfuncHRChartData: ChartDataSets[] = [];
  public situfuncSeekChartData: ChartDataSets[] = [];
  public situfuncnosChartData: ChartDataSets[] = [];
  public situfunchitshrChartData: ChartDataSets[] = [];
  public situfunchitsChartData: ChartDataSets[] = [];
  public situfuncmissesChartData: ChartDataSets[] = [];
  public situfunchrChartData: ChartDataSets[] = [];

  public aggfuncHRChartData: ChartDataSets[] = [];
  public aggfuncSeekChartData: ChartDataSets[] = [];
  public aggfuncnosChartData: ChartDataSets[] = [];
  public aggfunchitshrChartData: ChartDataSets[] = [];
  public aggfunchitsChartData: ChartDataSets[] = [];
  public aggfuncmissesChartData: ChartDataSets[] = [];
  public aggfunchrChartData: ChartDataSets[] = [];

  public crHRChartData: ChartDataSets[] = [];
  public crSeekChartData: ChartDataSets[] = [];
  public crnosChartData: ChartDataSets[] = [];
  public crhitshrChartData: ChartDataSets[] = [];
  public crhitsChartData: ChartDataSets[] = [];
  public crmissesChartData: ChartDataSets[] = [];
  public crhrChartData: ChartDataSets[] = [];

  public qHRChartData: ChartDataSets[] = [];
  public qSeekChartData: ChartDataSets[] = [];
  public qnosChartData: ChartDataSets[] = [];
  public qhitshrChartData: ChartDataSets[] = [];
  public qhitsChartData: ChartDataSets[] = [];
  public qmissesChartData: ChartDataSets[] = [];
  public qhrChartData: ChartDataSets[] = [];

  public ChartColors: Color[] = [
    {
      borderColor: 'rgba(255,0,0,0.8)', // Red
      backgroundColor: 'rgba(255,0,0,0.1)',
    },
    {
      borderColor: 'rgb(0, 194, 255, 0.8)', // Blue
      backgroundColor: 'rgb(0, 194, 255, 0.1)', // Bliw
    },
    {
      borderColor: 'rgb(17, 192, 45, 0.8)', // Green
      backgroundColor: 'rgb(17, 192, 45, 0.1)', // Green
    },
    {
      borderColor: 'rgb(234, 196, 1, 0.8)', // Yellow
      backgroundColor: 'rgb(234, 196, 1, 0.1)', // Yellow
    },
  ];

  public hrVariationChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Hit Rate'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Window Index'
        }
      }]
    }
  };

  public rtVariationChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Response Latency (ms)'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Window Index'
        }
      }]
    }
  };

  public itemsChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Number of Items'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Window Index'
        }
      }]
    }
  };

  public itemWiseChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Number of Items'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Item Index'
        }
      }]
    }
  };

  public itemHitsChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Number Hits'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Item Index'
        }
      }]
    }
  };

  public itemMissChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Number Misses'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Item Index'
        }
      }]
    }
  };

  public itemHRChart: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Hit Rate'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Item Index'
        }
      }]
    }
  };

  public scatterChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Delay Rate'
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Processing Overhead (ms)'
        }
      }]
    }
  };

  constructor(private serviceAPI: ApiServiceService) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  interval: any;

  ngOnInit() {
    this.initializeData();
    this.interval = setInterval(() => { 
        this.updateData(); 
    }, config.refresh_rate);
  }

  initializeData(){
    let data = this.serviceAPI.getCacheLevelPerformanceData();

    try{
      // Entity
      this.entity_hr = data.entity_hr.get();
      this.entity_hit_rt = data.entity_hit_rt.get();
      this.entity_miss_rt = data.entity_miss_rt.get();
      this.entity_cached = data.entity_cached.get();

      this.entityHRChartData.push({ data: this.entity_hr, label: 'Hit Rate' });
      this.entitySeekChartData.push({ data: this.entity_hit_rt, label: 'Cache Seek Time' });
      this.entitynosChartData.push({ data: this.entity_cached, label: 'Number of items Cached' });

      let entityScatter = {
        'data': data.entity_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.entityhitshrChartData.push(entityScatter);

      this.entityhitsChartData.push(
        { data: data.entity_items_hits.sort((a,b)=> a < b ? 1:-1), label: 'Hits'}
      );
      this.entitymissesChartData.push(
        { data: data.entity_items_miss.sort((a,b)=> a < b ? 1:-1), label: 'Misses'}
      );
      this.entityhrChartData.push(
        { data: data.entity_items_hr.sort((a,b)=> a < b ? 1:-1), label: 'Hit Rate'}
      );

      for(let i=0; i<data.entity_nos; i++){
        this.entitybarChartLabels.push(i.toString());
      }

      // Situation Function
      this.situfunc_hr = data.situfunc_hr.get();
      this.situfunc_hit_rt = data.situfunc_hit_rt.get();
      this.situfunc_miss_rt = data.situfunc_miss_rt.get();
      this.situfunc_cached = data.situfunc_cached.get();

      this.situfuncHRChartData.push({ data: this.situfunc_hr, label: 'Hit Rate' });
      this.situfuncSeekChartData.push({ data: this.situfunc_hit_rt, label: 'Cache Seek Time' });
      this.situfuncnosChartData.push({ data: this.situfunc_cached, label: 'Number of items Cached' });

      let situfuncScatter = {
        'data': data.situfunc_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.situfunchitshrChartData.push(situfuncScatter);

      this.situfunchitsChartData.push(
        { data: data.situfunc_items_hits.sort((a,b)=> a < b ? 1:-1), label: 'Hits'}
      );
      this.situfuncmissesChartData.push(
        { data: data.situfunc_items_miss.sort((a,b)=> a < b ? 1:-1), label: 'Misses'}
      );
      this.situfunchrChartData.push(
        { data: data.situfunc_items_hr.sort((a,b)=> a < b ? 1:-1), label: 'Hit Rate'}
      );

      for(let i=0; i<data.situfunc_nos; i++){
        this.situfuncbarChartLabels.push(i.toString());
      }

      // Aggregate Function
      this.aggfunc_hr = data.aggfunc_hr.get();
      this.aggfunc_hit_rt = data.aggfunc_hit_rt.get();
      this.aggfunc_miss_rt = data.aggfunc_miss_rt.get();
      this.aggfunc_cached = data.aggfunc_cached.get();

      this.aggfuncHRChartData.push({ data: this.aggfunc_hr, label: 'Hit Rate' });
      this.aggfuncSeekChartData.push({ data: this.aggfunc_hit_rt, label: 'Cache Seek Time' });
      this.aggfuncnosChartData.push({ data: this.aggfunc_cached, label: 'Number of items Cached' });

      let aggfuncScatter = {
        'data': data.aggfunc_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.aggfunchitshrChartData.push(aggfuncScatter);

      this.aggfunchitsChartData.push(
        { data: data.aggfunc_items_hits.sort((a,b)=> a < b ? 1:-1), label: 'Hits'}
      );
      this.aggfuncmissesChartData.push(
        { data: data.aggfunc_items_miss.sort((a,b)=> a < b ? 1:-1), label: 'Misses'}
      );
      this.aggfunchrChartData.push(
        { data: data.aggfunc_items_hr.sort((a,b)=> a < b ? 1:-1), label: 'Hit Rate'}
      );

      for(let i=0; i<data.aggfunc_nos; i++){
        this.aggfuncbarChartLabels.push(i.toString());
      }

      // Context Request
      this.cr_hr = data.cr_hr.get();
      this.cr_hit_rt = data.cr_hit_rt.get();
      this.cr_miss_rt = data.cr_miss_rt.get();
      this.cr_cached = data.cr_cached.get();

      this.crHRChartData.push({ data: this.cr_hr, label: 'Hit Rate' });
      this.crSeekChartData.push({ data: this.cr_hit_rt, label: 'Cache Seek Time' });
      this.crnosChartData.push({ data: this.cr_cached, label: 'Number of items Cached' });

      let crScatter = {
        'data': data.cr_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.crhitshrChartData.push(crScatter);

      this.crhitsChartData.push(
        { data: data.cr_items_hits.sort((a,b)=> a < b ? 1:-1), label: 'Hits'}
      );
      this.crmissesChartData.push(
        { data: data.cr_items_miss.sort((a,b)=> a < b ? 1:-1), label: 'Misses'}
      );
      this.crhrChartData.push(
        { data: data.cr_items_hr.sort((a,b)=> a < b ? 1:-1), label: 'Hit Rate'}
      );

      for(let i=0; i<data.cr_nos; i++){
        this.crbarChartLabels.push(i.toString());
      }

      // Context Query
      this.q_hr = data.q_hr.get();
      this.q_hit_rt = data.q_hit_rt.get();
      this.q_miss_rt = data.q_miss_rt.get();
      this.q_cached = data.q_cached.get();

      this.qHRChartData.push({ data: this.q_hr, label: 'Hit Rate' });
      this.qSeekChartData.push({ data: this.q_hit_rt, label: 'Cache Seek Time' });
      this.qnosChartData.push({ data: this.q_cached, label: 'Number of items Cached' });

      let qScatter = {
        'data': data.q_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.qhitshrChartData.push(qScatter);

      this.qhitsChartData.push(
        { data: data.q_items_hits.sort((a,b)=> a < b ? 1:-1), label: 'Hits'}
      );
      this.qmissesChartData.push(
        { data: data.q_items_miss.sort((a,b)=> a < b ? 1:-1), label: 'Misses'}
      );
      this.qhrChartData.push(
        { data: data.q_items_hr.sort((a,b)=> a < b ? 1:-1), label: 'Hit Rate'}
      );

      for(let i=0; i<data.q_nos; i++){
        this.qbarChartLabels.push(i.toString());
      }

      // General
      this.timeTicks = data.timeTicks.get();

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

  updateData(){
    let data = this.serviceAPI.getCacheLevelPerformanceData();

    try{
      // Entity
      this.entity_hr = data.entity_hr.get();
      this.entity_hit_rt = data.entity_hit_rt.get();
      this.entity_miss_rt = data.entity_miss_rt.get();
      this.entity_cached = data.entity_cached.get();

      this.entityHRChartData[0].data = this.entity_hr;
      this.entitySeekChartData[0].data = this.entity_hit_rt;
      this.entitynosChartData[0].data = this.entity_cached;

      let entityScatter = {
        'data': data.entity_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.entityhitshrChartData.splice(0, this.entityhitshrChartData.length);
      this.entityhitshrChartData.push(entityScatter);

      this.entityhitsChartData[0].data = data.entity_items_hits.sort((a,b)=> a < b ? 1:-1);
      this.entitymissesChartData[0].data = data.entity_items_miss.sort((a,b)=> a < b ? 1:-1);
      this.entityhrChartData[0].data = data.entity_items_hr.sort((a,b)=> a < b ? 1:-1);

      // Situation Function
      this.situfunc_hr = data.situfunc_hr.get();
      this.situfunc_hit_rt = data.situfunc_hit_rt.get();
      this.situfunc_miss_rt = data.situfunc_miss_rt.get();
      this.situfunc_cached = data.situfunc_cached.get();

      this.situfuncHRChartData[0].data = this.situfunc_hr;
      this.situfuncSeekChartData[0].data = this.situfunc_hit_rt;
      this.situfuncnosChartData[0].data = this.situfunc_cached;

      let situfuncScatter = {
        'data': data.situfunc_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.situfunchitshrChartData.splice(0, this.situfunchitshrChartData.length);
      this.situfunchitshrChartData.push(situfuncScatter);

      this.situfunchitsChartData[0].data = data.situfunc_items_hits.sort((a,b)=> a < b ? 1:-1);
      this.situfuncmissesChartData[0].data = data.situfunc_items_miss.sort((a,b)=> a < b ? 1:-1);
      this.situfunchrChartData[0].data = data.situfunc_items_hr.sort((a,b)=> a < b ? 1:-1);

      // Aggregate Function
      this.aggfunc_hr = data.aggfunc_hr.get();
      this.aggfunc_hit_rt = data.aggfunc_hit_rt.get();
      this.aggfunc_miss_rt = data.aggfunc_miss_rt.get();
      this.aggfunc_cached = data.aggfunc_cached.get();

      this.aggfuncHRChartData[0].data = this.aggfunc_hr;
      this.aggfuncSeekChartData[0].data = this.aggfunc_hit_rt;
      this.aggfuncnosChartData[0].data = this.aggfunc_cached;

      let aggfuncScatter = {
        'data': data.aggfunc_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.aggfunchitshrChartData.splice(0, this.aggfunchitshrChartData.length);
      this.aggfunchitshrChartData.push(aggfuncScatter);

      this.aggfunchitsChartData[0].data = data.aggfunc_items_hits.sort((a,b)=> a < b ? 1:-1);
      this.aggfuncmissesChartData[0].data = data.aggfunc_items_miss.sort((a,b)=> a < b ? 1:-1);
      this.aggfunchrChartData[0].data = data.aggfunc_items_hr.sort((a,b)=> a < b ? 1:-1);

      // Context Request
      this.cr_hr = data.cr_hr.get();
      this.cr_hit_rt = data.cr_hit_rt.get();
      this.cr_miss_rt = data.cr_miss_rt.get();
      this.cr_cached = data.cr_cached.get();

      this.crHRChartData[0].data = this.cr_hr;
      this.crSeekChartData[0].data = this.cr_hit_rt;
      this.crnosChartData[0].data = this.cr_cached;

      let crScatter = {
        'data': data.cr_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };
      this.crhitshrChartData.splice(0, this.crhitshrChartData.length);
      this.crhitshrChartData.push(crScatter);

      this.crhitsChartData[0].data = data.cr_items_hits.sort((a,b)=> a < b ? 1:-1);
      this.crmissesChartData[0].data = data.cr_items_miss.sort((a,b)=> a < b ? 1:-1);
      this.crhrChartData.push[0].data = data.cr_items_hr.sort((a,b)=> a < b ? 1:-1);

      // Context Query
      this.q_hr = data.q_hr.get();
      this.q_hit_rt = data.q_hit_rt.get();
      this.q_miss_rt = data.q_miss_rt.get();
      this.q_cached = data.q_cached.get();

      this.qHRChartData[0].data = this.q_hr;
      this.qSeekChartData[0].data = this.q_hit_rt;
      this.qnosChartData[0].data = this.q_cached;

      let qScatter = {
        'data': data.q_hits_hr,
        'label': 'Hits - Hit Rate',
        'pointRadius': 5 
      };

      this.qhitshrChartData.splice(0, this.qhitshrChartData.length);
      this.qhitshrChartData.push(qScatter);

      this.qhitsChartData[0].data = data.q_items_hits.sort((a,b)=> a < b ? 1:-1);
      this.qmissesChartData[0].data = data.q_items_miss.sort((a,b)=> a < b ? 1:-1);
      this.qhrChartData[0].data = data.q_items_hr.sort((a,b)=> a < b ? 1:-1);

      // General
      this.entitybarChartLabels.splice(0, this.entitybarChartLabels.length);
      for(let i=0; i<data.entity_nos; i++){
        this.entitybarChartLabels.push(i.toString());
      }

      this.situfuncbarChartLabels.splice(0, this.situfuncbarChartLabels.length);
      for(let i=0; i<data.situfunc_nos; i++){
        this.situfuncbarChartLabels.push(i.toString());
      }

      this.aggfuncbarChartLabels.splice(0, this.aggfuncbarChartLabels.length);
      for(let i=0; i<data.aggfunc_nos; i++){
        this.aggfuncbarChartLabels.push(i.toString());
      }

      this.crbarChartLabels.splice(0, this.crbarChartLabels.length);
      for(let i=0; i<data.cr_nos; i++){
        this.crbarChartLabels.push(i.toString());
      }

      this.qbarChartLabels.splice(0, this.qbarChartLabels.length);
      for(let i=0; i<data.q_nos; i++){
        this.qbarChartLabels.push(i.toString());
      }

      this.timeTicks = data.timeTicks.get();

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

}

import { Component, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip, SingleDataSet } from 'ng2-charts';
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
  public barChartLabels: Label[] = [];

  public entityHRChartData: ChartDataSets[] = [];
  public entitySeekChartData: ChartDataSets[] = [];
  public entitynosChartData: ChartDataSets[] = [];
  public entityhitshrChartData: ChartDataSets[] = [];
  public entityhitsChartData: ChartDataSets[] = [];
  public entitymissesChartData: ChartDataSets[] = [];
  public entityhrChartData: ChartDataSets[] = [];

  public ChartColors: Color[] = [
    {
      backgroundColor: 'rgba(255,0,0,0.6)', // Red
    },
    {
      backgroundColor: 'rgb(0, 194, 255, 0.6)', // Bliw
    },
    {
      backgroundColor: 'rgb(17, 192, 45, 0.6)', // Green
    },
    {
      backgroundColor: 'rgb(234, 196, 1, 0.6)', // Yellow
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

  ngOnInit() {
    this.updateData();
  }

  updateData(){
    let data = this.serviceAPI.getCacheLevelPerformanceData();

    try{
      this.barChartLabels = [];
      
      // Top bar
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

      // General
      let sizeofArray = entityScatter == undefined ? 1000 : entityScatter.data.length;
      for(let i=0; i<sizeofArray; i++){
        this.barChartLabels.push(i.toString());
      }

      this.timeTicks = data.timeTicks.get();

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

}

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ChartData, ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip, SingleDataSet } from 'ng2-charts';
import { config } from '../config';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-overall',
  templateUrl: './overall.component.html',
  styleUrls: ['./overall.component.css']
})
export class OverallComponent implements OnInit {

  gain;
  avg_gain;
  cache;
  model;

  earning;
  penalty_cost;
  retrieval_cost;
  processing_cost;
  cache_cost;

  costearningratio;

  no_of_queries;
  no_of_retrievals;
  avg_query_overhead;
  avg_network_overhead;
  avg_processing_overhead;
  processing_overhead_ratio;
  network_overhead_ratio;

  total_cache;
  occupied_cache;
  data_occupied_cache;

  exp_pen;
  exp_fthr;
  exp_earn;
  exp_rtmax;

  kappa;
  mu;
  pi;
  delta;
  row;
  threshold;

  reward;

  query_latency;

  timeTicks;

  isGreater = false;

  public lineChartType = 'line';
  public pieChartType: ChartType = 'pie';
  public scatterChartType: ChartType = 'scatter';
  public doughnutChartType: ChartType = 'doughnut';

  public lineChartData: ChartDataSets[] = [];
  public line2ChartData: ChartDataSets[] = [];
  public line3ChartData: ChartDataSets[] = [];
  public line4ChartData: ChartDataSets[] = [];
  public line5ChartData: ChartDataSets[] = [];
  public line6ChartData: ChartDataSets[] = [];
  public line7ChartData: ChartDataSets[] = [];
  public line8ChartData: ChartDataSets[] = [];

  public pieChartData: SingleDataSet; 
  public pie2ChartData: SingleDataSet; 

  public scatter1Data = {
    'data': [],
    'label': 'Query Latency - Delay Rate',
    'pointRadius': 5 
  };
  public scatter2Data = {
    'data': [],
    'label': 'Network Overhead - Delay Rate',
    'pointRadius': 5 
  };
  public scatter3Data = {
    'data': [],
    'label': 'Processing Overhead - Delay Rate',
    'pointRadius': 5 
  };

  public scatter4Data = {
    'data': [],
    'label': 'Threshold - Reward',
    'pointRadius': 5 
  };
  public scatter5Data = {
    'data': [],
    'label': 'Threshold - Cache Life',
    'pointRadius': 5 
  };
  public scatter6Data = {
    'data': [],
    'label': 'Threshold - Delay Time',
    'pointRadius': 5 
  };

  public scatter1ChartData: ChartDataSets[] = [];
  public scatter2ChartData: ChartDataSets[] = [];
  public scatter3ChartData: ChartDataSets[] = [];
  public scatter4ChartData: ChartDataSets[] = [];
  public scatter5ChartData: ChartDataSets[] = [];
  public scatter6ChartData: ChartDataSets[] = [];
  
  public pieChartLegend = true;
  public pieChartLabels: Label[] = ['Earning', 'Retrieval Cost', 'Penalty Cost', 'Process Cost', 'Cache Cost'];
  public pie2ChartLabels: Label[] = ['Unoccupied','Data Occupied', 'Overhead Occupied'];
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartPlugins = [];

  public ChartColors: Color[] = [
    {
      borderColor: 'rgb(0, 194, 255, 0.8)', // Blue
      backgroundColor: 'rgb(0, 194, 255, 0.1)', // Blue
    },
    {
      borderColor: 'rgb(17, 192, 45, 0.8)', // Green
      backgroundColor: 'rgb(17, 192, 45, 0.1)', // Green
    },
    {
      borderColor: 'rgb(234, 196, 1, 0.8)', // Yellow
      backgroundColor: 'rgb(234, 196, 1, 0.1)', // Yellow
    },
    {
      borderColor: 'rgba(255,0,0,0.8)', // Red
      backgroundColor: 'rgba(248, 5, 50, 0.1)',
    },
    {
      borderColor: 'rgba(134, 17, 192, 0.8)', // Purple
      backgroundColor: 'rgba(134, 17, 192, 0.1)',
    },
    {
      borderColor: 'rgba(245, 137, 14, 0.8)', // Orange
      backgroundColor: 'rgba(245, 137, 14, 0.1)',
    }
  ];

  public pieChartColors: Color[] = [
    {
      backgroundColor: ['rgb(0, 194, 255, 0.6)', 'rgb(17, 192, 45, 0.6)', 'rgb(234, 196, 1, 0.6)',
      'rgba(248, 5, 50, 0.6)', 'rgba(134, 17, 192, 0.6)', 'rgba(245, 137, 14, 0.6)']
    }
  ];

  public lineChartLabels: Label[];

  public lineChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'AUD'
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

  public line2ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Time (miliseconds)'
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

  public line3ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Ratio'
        },
        ticks: {
          beginAtZero: true 
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

  public line5ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Size (GB)'
        },
        ticks: {
          beginAtZero: true 
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

  public line6ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Freshness Threshold'
        },
        ticks: {
          beginAtZero: true 
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

  public line7ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Parameter Value'
        },
        ticks: {
          beginAtZero: true 
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

  public line8ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Reward (AUD)'
        },
        ticks: {
          beginAtZero: true 
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

  public scatter1ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Delay Rate'
        },
        ticks: {
          beginAtZero: true 
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Query Latency (ms)'
        }
      }]
    }
  };

  public scatter2ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Delay Rate'
        },
        ticks: {
          beginAtZero: true 
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Network Overhard (ms)'
        }
      }]
    }
  };

  public scatter3ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Delay Rate'
        },
        ticks: {
          beginAtZero: true 
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

  public scatter4ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Reward (AUD)'
        },
        ticks: {
          beginAtZero: false 
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Cache Threshold'
        },
        ticks: {
          beginAtZero: true 
        }
      }]
    }
  };

  public scatter5ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Cache Life (ms)'
        },
        ticks: {
          beginAtZero: false 
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Cache Threshold'
        },
        ticks: {
          beginAtZero: true 
        }
      }]
    }
  };

  public scatter6ChartOptions: (ChartOptions) = {
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Delay Time (ms)'
        },
        ticks: {
          beginAtZero: false 
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Cache Threshold'
        },
        ticks: {
          beginAtZero: true 
        }
      }]
    }
  };

  public lineChartLegend = true;
  public lineChartPlugins = [];

  interval:any;

  constructor(private serviceAPI: ApiServiceService, private ref: ChangeDetectorRef) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() { 
    this.initializeData();
    this.interval = setInterval(() => { 
        this.updateData(); 
    }, config.refresh_rate);
  }

  initializeData(){
    let data = this.serviceAPI.getPerformanceSummary(); 
    let model = this.serviceAPI.getModelVariation();

    try{
      // Top bar
      this.gain = data.gain.get();
      this.avg_gain = data.avg_gain.get()
      this.no_of_queries = data.no_of_queries.get();
      this.no_of_retrievals = data.no_of_retrievals.get();
      // + current average query latency

      // 2nd
      this.lineChartData.push({ data: this.gain, label: 'Gain' });
      // + current gain

      // 3rd - Left
      this.earning = data.earning.get();
      this.lineChartData.push({ data: this.earning, label: 'Earning' });   
      this.retrieval_cost = data.retrieval_cost.get();
      this.lineChartData.push({ data: this.retrieval_cost, label: 'Retrieval Cost' });
      this.penalty_cost = data.penalty_cost.get();
      this.lineChartData.push({ data: this.penalty_cost, label: 'Penalties' });
      this.processing_cost = data.processing_cost.get();
      this.lineChartData.push({ data: this.processing_cost, label: 'Process Cost' });
      this.cache_cost = data.cache_cost.get();
      this.lineChartData.push({ data: this.cache_cost, label: 'Cache Cost' });

      // 4th Line
      this.exp_pen = data.exp_pen.get();
      this.exp_fthr = data.exp_fth.get();
      this.exp_earn = data.exp_earn.get();
      this.exp_rtmax = data.exp_rtmax.get();

      // 3rd - Right
      // + current earning
      // + current penalty
      // + current retriveal cost 

      this.pieChartData = data.currentCosts;

      this.costearningratio = data.costearningratio.get();
      this.line4ChartData.push({ data: this.costearningratio, label: 'Cost to Earning Ration' }); 
      this.line6ChartData.push({ data: this.exp_fthr, label: 'Freshness Threshold' }); 

      // 4th 
      this.avg_query_overhead = data.avg_query_overhead.get();
      this.line2ChartData.push({ data: this.avg_query_overhead, label: 'Query Latency' }); 

      this.avg_network_overhead = data.avg_network_overhead.get();
      this.line2ChartData.push({ data: this.avg_network_overhead, label: 'Network Overhead' }); 

      this.avg_processing_overhead = data.avg_processing_overhead.get();
      this.line2ChartData.push({ data: this.avg_processing_overhead, label: 'Processing Overhead' }); 

      this.processing_overhead_ratio = data.processing_overhead_ratio.get();
      this.line3ChartData.push({ data: this.processing_overhead_ratio, label: 'Processing Overhead Ratio' }); 

      this.network_overhead_ratio = data.network_overhead_ratio.get();
      this.line3ChartData.push({ data: this.network_overhead_ratio, label: 'Network Overhead Ratio' }); 

      // 5th
      this.scatter1Data.data = data.rt_pod;
      this.scatter1ChartData.push(this.scatter1Data);

      this.scatter2Data.data = data.noh_pod;
      this.scatter2ChartData.push(this.scatter2Data);

      this.scatter3Data.data = data.poh_pod;
      this.scatter3ChartData.push(this.scatter3Data);

      // 6th
      this.cache = data.cache;
      this.total_cache = data.total_cache.get();
      this.line5ChartData.push({ data: this.total_cache, label: 'Total Cache Size' }); 

      this.occupied_cache = data.occupied_cache.get();
      this.line5ChartData.push({ data: this.occupied_cache, label: 'Size Occupied' }); 

      this.data_occupied_cache = data.data_occupied_cache.get();
      this.line5ChartData.push({ data: this.data_occupied_cache, label: 'Sizd Data Occupied' }); 

      this.pie2ChartData = data.cache_occupancy;

      // 7th
      this.kappa = model.kappa.get();
      this.line7ChartData.push({ data: this.kappa, label: 'Kappa' }); 
      this.mu = model.mu.get();
      this.line7ChartData.push({ data: this.mu, label: 'Mu' }); 
      this.pi = model.pi.get();
      this.line7ChartData.push({ data: this.pi, label: 'Pi' }); 
      this.delta = model.delta.get();
      this.line7ChartData.push({ data: this.delta, label: 'Delta' }); 
      this.row = model.row.get();
      this.line7ChartData.push({ data: this.row, label: 'Row' }); 
      this.threshold = model.threshold.get();
      this.line7ChartData.push({ data: this.threshold, label: 'Threshold' }); 

      this.reward = model.avg_reward.get();
      this.line8ChartData.push({ data: this.reward, label: 'Average Gain (Reward)' }); 

      //9th 
      this.scatter4Data.data = model.reward_th;
      this.scatter4ChartData.push(this.scatter4Data);

      this.scatter5Data.data = model.cl_th;
      this.scatter5ChartData.push(this.scatter5Data);

      this.scatter6Data.data = model.dt_th;
      this.scatter6ChartData.push(this.scatter6Data);

      // General
      this.timeTicks = data.timeTicks.get();

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

  updateData() {
    try{
      let data = this.serviceAPI.getPerformanceSummary(); 
      let model = this.serviceAPI.getModelVariation();

      // Top bar
      this.gain = data.gain.get();
      this.avg_gain = data.avg_gain.get()
      this.no_of_queries = data.no_of_queries.get();
      this.no_of_retrievals = data.no_of_retrievals.get();

      // 2nd
      this.lineChartData[0].data = this.gain;

      // 3rd - Left
      this.earning = data.earning.get();
      this.lineChartData[1].data = this.earning;   

      this.retrieval_cost = data.retrieval_cost.get();
      this.lineChartData[2].data = this.retrieval_cost;

      this.penalty_cost = data.penalty_cost.get();
      this.lineChartData[3].data = this.penalty_cost;

      this.processing_cost = data.processing_cost.get();
      this.lineChartData[4].data = this.processing_cost;

      this.cache_cost = data.cache_cost.get();
      this.lineChartData[5].data = this.cache_cost;

      this.costearningratio = data.costearningratio.get();
      this.line4ChartData[0].data = this.costearningratio; 

      // 4th Line
      this.exp_pen = data.exp_pen.get();
      this.exp_fthr = data.exp_fth.get();
      this.exp_earn = data.exp_earn.get();
      this.exp_rtmax = data.exp_rtmax.get();
      this.line6ChartData[0].data = this.exp_fthr; 

      // 4th 
      this.avg_query_overhead = data.avg_query_overhead.get();
      this.line2ChartData[0].data = this.avg_query_overhead; 

      this.avg_network_overhead = data.avg_network_overhead.get();
      this.line2ChartData[1].data = this.avg_network_overhead; 

      this.avg_processing_overhead = data.avg_processing_overhead.get();
      this.line2ChartData[2].data = this.avg_processing_overhead; 

      this.processing_overhead_ratio = data.processing_overhead_ratio.get();
      this.line3ChartData[0].data = this.processing_overhead_ratio; 

      this.network_overhead_ratio = data.network_overhead_ratio.get();
      this.line3ChartData[1].data = this.network_overhead_ratio; 

      // General
      this.timeTicks = data.timeTicks.get();

      // 3rd - Right
      this.pieChartData = data.currentCosts;

      // 5th
      this.scatter1Data.data = data.rt_pod;
      this.scatter1ChartData.push(this.scatter1Data);

      this.scatter2Data.data = data.noh_pod;
      this.scatter2ChartData.push(this.scatter2Data);

      this.scatter3Data.data = data.poh_pod;
      this.scatter3ChartData.push(this.scatter3Data);

      // 6th
      this.cache = data.cache;

      this.total_cache = data.total_cache.get();
      this.line5ChartData[0].data = this.total_cache; 

      this.occupied_cache = data.occupied_cache.get();
      this.line5ChartData[1].data = this.occupied_cache; 

      this.data_occupied_cache = data.data_occupied_cache.get();
      this.line5ChartData[2].data = this.data_occupied_cache; 

      this.pie2ChartData = data.cache_occupancy;

      //7th
      this.kappa = model.kappa.get();
      this.line7ChartData[0].data = this.kappa; 

      this.mu = model.mu.get();
      this.line7ChartData[1].data = this.mu;

      this.pi = model.pi.get();
      this.line7ChartData[2].data = this.pi; 

      this.delta = model.delta.get();
      this.line7ChartData[3].data = this.delta; 

      this.row = model.row.get();
      this.line7ChartData[4].data = this.row; 

      this.threshold = model.threshold.get();
      this.line7ChartData[5].data = this.threshold; 

      this.reward = model.avg_reward.get();
      this.line8ChartData[0].data = this.reward; 

      //9th
      this.scatter4Data.data = model.reward_th;
      this.scatter4ChartData.push(this.scatter4Data);

      this.scatter5Data.data = model.cl_th;
      this.scatter5ChartData.push(this.scatter5Data);

      this.scatter6Data.data = model.dt_th;
      this.scatter6ChartData.push(this.scatter6Data);

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

}

import { Component, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, Label } from 'ng2-charts';
import { ApiServiceService } from '../services/api-service.service';
import { SummaryModel } from '../services/service-view-models';

@Component({
  selector: 'app-overall',
  templateUrl: './overall.component.html',
  styleUrls: ['./overall.component.css']
})
export class OverallComponent implements OnInit {

  private apiService;

  gain;
  avg_gain

  earning;
  penalty_cost;
  retrieval_cost;

  costearningratio;

  no_of_queries;
  no_of_retrievals;
  avg_query_overhead;
  avg_network_overhead;
  avg_processing_overhead;
  processing_overhead_ratio;
  network_overhead_ratio;

  query_latency;

  timeTicks;

  isGreater = false;

  public lineChartData: ChartDataSets[] = [];
  public line2ChartData: ChartDataSets[] = [];
  public line3ChartData: ChartDataSets[] = [];
  public line4ChartData: ChartDataSets[] = [];

  public pieChartData: ChartDataSets[] = [];
  public pieChartLabels: Label[] = ['Gain', 'Earning', 'Retrieval Cost', 'Penalty Cost'];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = false;

  public doughnutChartType: ChartType = 'doughnut';
  
  public ChartColors: Color[] = [
    {
      backgroundColor: 'rgba(255,0,0,0.6)',
    },
    {
      backgroundColor: 'rgb(0, 194, 255, 0.6)',
    },
    {
      backgroundColor: 'rgb(17, 192, 45, 0.6)',
    },
    {
      backgroundColor: 'rgb(234, 196, 1, 0.6)',
    },
  ];

  public pieChartColors: Color[] = [
    {
      backgroundColor: 'rgb(0, 194, 255, 0.6)',
    },
    {
      backgroundColor: 'rgb(17, 192, 45, 0.6)',
    },
    {
      backgroundColor: 'rgb(234, 196, 1, 0.6)',
    },
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

  public lineChartLegend = true;
  public lineChartType = 'line';
  public lineChartPlugins = [];

  constructor(private serviceAPI: ApiServiceService) {}

  ngOnInit() {  
    let data = this.serviceAPI.getPerformanceSummary(); 

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

      // 3rd - Right
      // + current earning
      // + current penalty
      // + current retriveal cost 
      this.costearningratio = data.costearningratio.get();
      this.line4ChartData.push({ data: this.costearningratio, label: 'Cost to Earning Ration' }); 

      // 4th 
      this.avg_query_overhead = data.avg_query_overhead.get();
      this.line2ChartData.push({ data: this.avg_query_overhead, label: 'Query Latency' }); 

      this.avg_network_overhead = data.avg_network_overhead.get();
      this.line2ChartData.push({ data: this.avg_network_overhead, label: 'Network Latency' }); 

      this.avg_processing_overhead = data.avg_processing_overhead.get();
      this.line2ChartData.push({ data: this.avg_processing_overhead, label: 'Processing Latency' }); 

      this.processing_overhead_ratio = data.processing_overhead_ratio.get();
      this.line3ChartData.push({ data: this.earning, label: 'Processing Overhead Ratio' }); 

      this.network_overhead_ratio = data.network_overhead_ratio.get();
      this.line3ChartData.push({ data: this.earning, label: 'Network Overhead Ratio' }); 

      // General
      this.timeTicks = data.timeTicks.get();
      this.assignData();
    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }
  }

  assignData(){
    this.pieChartData.push(this.earning[this.earning-1]);
    this.pieChartData.push(this.retrieval_cost[this.retrieval_cost-1]);
    this.pieChartData.push(this.penalty_cost[this.penalty_cost-1]);
    
    console.log('NO:'+this.network_overhead_ratio);

  }

}

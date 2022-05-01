import { Component, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Color, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip, SingleDataSet } from 'ng2-charts';
import { ApiServiceService } from '../services/api-service.service';

@Component({
  selector: 'app-csms',
  templateUrl: './csms.component.html',
  styleUrls: ['./csms.component.css']
})
export class CSMSComponent implements OnInit {

  hcr_ok;
  hcr_all;
  hcr_avg;
  hcr_sucess;

  dms_ok;
  dms_all;
  dms_avg;
  dms_sucess;

  rce_ok;
  rce_all;
  rce_avg;
  rce_sucess;

  timeTicks;

  public hcrChartData: SingleDataSet; 
  public dmsChartData: SingleDataSet; 
  public rceChartData: SingleDataSet; 

  public hcrlineChartData: ChartDataSets[] = [];
  public dmslineChartData: ChartDataSets[] = [];
  public rcelineChartData: ChartDataSets[] = [];

  public lineChartType = 'line';
  public doughnutChartType: ChartType = 'doughnut';

  public pieChartLabels: Label[] = ['Successful', 'Errorneous'];
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartColors: Color[] = [
    {
      backgroundColor: ['rgb(0, 194, 255, 0.6)', 'rgb(17, 192, 45, 0.6)', 'rgb(234, 196, 1, 0.6)']
    }
  ];
  public pieChartPlugins = [];

  public ChartColors: Color[] = [
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
          labelString: 'Number'
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
  public lineChartPlugins = [];

  constructor(private serviceAPI: ApiServiceService) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() {
    this.updateData();
  }

  updateData() {
    let data = this.serviceAPI.getCSMSPerformanceData();
    try{
      // Top bar
      this.hcr_ok = data.hcr_ok.get();
      this.hcr_all = data.hcr_all.get();
      this.hcr_avg = data.hcr_avg.get();
      this.hcr_sucess = data.hcr_sucess.get();
      this.hcrChartData = data.cur_hcr_break;
      this.hcrlineChartData.push({ data: this.hcr_ok, label: 'Successful' });
      this.hcrlineChartData.push({ data: this.hcr_all, label: 'Total' });

      // 2nd
      this.dms_ok = data.dms_ok.get();
      this.dms_all = data.dms_all.get();
      this.dms_avg = data.dms_avg.get();
      this.dms_sucess = data.dms_sucess.get();
      this.dmsChartData = data.cur_dms_break;
      this.dmslineChartData.push({ data: this.dms_ok, label: 'Successful' });
      this.dmslineChartData.push({ data: this.dms_all, label: 'Total' });

      // 3rd 
      this.rce_ok = data.rce_ok.get();
      this.rce_all = data.rce_all.get();
      this.rce_avg = data.rce_avg.get();
      this.rce_sucess = data.rce_sucess.get();
      this.rceChartData = data.cur_rce_break;
      this.rcelineChartData.push({ data: this.rce_ok, label: 'Successful' });
      this.rcelineChartData.push({ data: this.rce_all, label: 'Total' });
    
      // General
      this.timeTicks = data.timeTicks.get();

    }
    catch(ex){
      // Code here
      console.log('An error occured!'+ ex);
    }

  }

}



import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {TrackingRecord} from '../objects/tracking-record';
import {TrackingRecordService} from '../services/tracking-record.service';
import {BaseChartDirective} from 'ng2-charts/ng2-charts';
import {ActivatedRoute} from '@angular/router';


@Component({
  selector: 'app-vehicle-detail',
  templateUrl: './vehicle-detail.component.html',
  styleUrls: ['./vehicle-detail.component.css']
})
export class VehicleDetailComponent implements OnInit {

  vin: string;

  view;

  records: TrackingRecord[];
  lat;
  lng;


  @ViewChild(BaseChartDirective) chart: BaseChartDirective;

  // lineChart
  public lineChartData: Array<any>;

  public lineChartLabels: Array<any> = new Array();

  public lineChartOptions: any = {
    responsive: true
  };
  public lineChartColors: Array<any> = [
    {
      backgroundColor: 'rgba(255,0,0,0.2)',
      borderColor: 'rgba(255,0,0,1)',
      pointBackgroundColor: 'rgba(255,0,0,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(255,0,0,0.8)'
    }
  ];
  public lineChartLegend: Boolean = false;
  public lineChartType: String = 'line';

  constructor(private trackingRecordService: TrackingRecordService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.view = localStorage.getItem('fleetName');
    this.vin = this.route.snapshot.paramMap.get('vin');
    this.trackingRecordService.getVehicleRecords(this.vin).subscribe(response => {
      this.records = response;
      if (response.length > 0) {
        const record = this.records[this.records.length - 1];
        this.lat = record.lat;
        this.lng = record.lon;
        this.initChartData(response);
      }
    });
  }

  initChartData(response: TrackingRecord[]) {
    this.lineChartData = [
      {data: [], label: 'Gefahrene Kilometer'},
    ];
    this.lineChartLabels = [];


    for (let _i = 0; _i < response.length; _i++) {
      this.lineChartLabels.push(response[_i].eventTime.substr(0,5));
      this.lineChartData[0].data[_i] = response[_i].distance;
    }
  }
}

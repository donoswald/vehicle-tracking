import {Component, OnInit} from '@angular/core';
import {TrackingRecord} from '../objects/tracking-record';
import {TrackingRecordService} from '../services/tracking-record.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-fleet-overview',
  templateUrl: './fleet-overview.component.html',
  styleUrls: ['./fleet-overview.component.css']
})
export class FleetOverviewComponent implements OnInit {

  records: TrackingRecord[] ;

  selected: TrackingRecord;

  constructor(private trackRecordService: TrackingRecordService, private router: Router) {
  }

  ngOnInit() {
    this.trackRecordService.getFleetRecords().subscribe(response => {
      console.log(response);
      this.records = response;
    });
  }

  onClick(item: TrackingRecord) {
    this.router.navigate(['/vehicle/' + item.vin]);
  }
}

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TrackingRecord} from '../objects/tracking-record';

@Injectable({
  providedIn: 'root'
})
export class TrackingRecordService {

  url = '/vehicles';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    })
  };

  constructor(private http: HttpClient) {
  }

  getFleetRecords(): Observable<TrackingRecord[]> {
    return this.http.get<TrackingRecord[]>(this.url, this.httpOptions);
  }

  getVehicleRecords(vin: String): Observable<TrackingRecord[]> {
    return this.http.get<TrackingRecord[]>(this.url + '/' + vin);
  }
}

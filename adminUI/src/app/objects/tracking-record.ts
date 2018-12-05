export class TrackingRecord {
  // ALREADY FORMATTED TO dd.MM.YYYY HH:mm , the time from GPS tracker (UTC)
  eventTime: string;
  // Vehicle Identification Number, ( primary key)
  vin: string;
  plateNumber: string;
    // KMs
  distance: number;
  // location
  lat: number;
  lon: number;
  // remaining fuel in liters
  remainingFuel: number;
  // full weight of vehicle and goods in KGs
  weight: number;
  // Oil temperature
  oilTemp: number;
  // Water temperature
  waterTemp: number;
  // motor running in milliseconds
  motorRunningTime: number;
}


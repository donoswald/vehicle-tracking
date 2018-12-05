# Vehicle

The Vehicle is a very simple Java main program, which runs endlessly. 
For starting, a runtime argument (the vin number) is needed: *java -cp trivadis.vehicle.Vehicle vin123*

It st constantly sends post request to http://localhost:9090/vehicles/insert 
containing a TrackingRecord filled with some random data. The authentication is done using the *PlanetExpress:secret* credentials.

package trivadis.vehicle;


public class Generator {

    static void init(TrackingRecord record) {

        record.setPlateNumber("ZH-"+Math.round(Math.random()*1000)+"-"+Math.round(Math.random()*1000));
        record.setLat(47 + Math.round(Math.random() * 0.01));
        record.setLon(8.5 + Math.round(Math.random() * 0.001));

        record.setDistance(Math.round(Math.random() * 1000000));
        record.setRemainingFuel(120+Math.round(Math.random() * 1200));
        record.setWeight(Math.round(Math.random() * 6000));
        record.setWaterTemp(Math.random() * 100);
        record.setOilTemp(Math.random() * 120);
        record.setMotorRunningTime(Math.round(Math.random() * 1000 * 3600 * 100));
    }

    static TrackingRecord next(TrackingRecord record) {
        TrackingRecord next = new TrackingRecord(record.getVin());

        next.setPlateNumber(record.getPlateNumber());
        next.setLat(record.getLat() + Math.random() * 0.3);
        next.setLon(record.getLon() + Math.random() * 0.3);

        next.setDistance(record.getDistance() + (long) (Math.random() * 100));
        next.setRemainingFuel(record.getRemainingFuel() - Math.round(Math.random() * 20));
        next.setMotorRunningTime(record.getMotorRunningTime() + 5000 * 60 * 60);

        long load = Math.round(Math.random() * 250);
        if (Math.random() < 0.5) {
            load = -load;
        }
        next.setWeight(record.getWeight() + load);
        next.setWaterTemp(record.getWaterTemp()+Math.random() * 10);
        next.setOilTemp(record.getOilTemp()+Math.random() * 12);
        return next;
    }

}

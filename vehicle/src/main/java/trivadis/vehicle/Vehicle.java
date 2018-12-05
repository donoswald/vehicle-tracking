package trivadis.vehicle;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Vehicle {
    private static final Logger log = LoggerFactory.getLogger(Vehicle.class.getName());

    private static final CustomObjectMapper om = new CustomObjectMapper();

    public static void main(String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException("expecting a vin number as first runtime argument");
        }

        TrackingRecord record = new TrackingRecord(args[0]);

        Generator.init(record);
        send(record);
        TrackingRecord next = null;
        while (true) {
            waitMs(5000);
            next = Generator.next(next == null ? record : next);
            send(next);
        }

    }

    private static void waitMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void send(TrackingRecord record) {
        try {
            log.info("sending {}", om.writeValueAsString(record));

            URL url = new URL("http://localhost:9090/vehicles/insert");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            String authStr = "PlanetExpress" + ":" + "secret";
            byte[] bytesEncoded = Base64.encodeBase64(authStr.getBytes());
            String authEncoded = new String(bytesEncoded);
            http.setRequestProperty("Authorization", "Basic " + authEncoded);

            http.setDoOutput(true);
            http.setRequestMethod("POST");
            OutputStream out = http.getOutputStream();

            om.writeValue(out, record);
            out.flush();
            out.close();

            log.info("response code {}", http.getResponseCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

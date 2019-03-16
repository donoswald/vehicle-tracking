package trivadis.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trivadis.dto.TrackingRecord;
import trivadis.hyperledger.LedgerApi;
import trivadis.hyperledger.SdkConfig;
import trivadis.simpleserver.CustomObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VehicleHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(VehicleHandler.class.getName());

    private static final LedgerApi api = LedgerApi.instance();
    private static final ObjectMapper om = new CustomObjectMapper();

    public static final String ORG1 = "InsuraAG";
    public static final String ORG2 = "PlanetExpress";

    public final static String HANDLER_URL = "vehicles";
    private static final String INSERT_PATH = "insert";


    private Map<String, HFClient> clients = new HashMap<>();

    public VehicleHandler() {
        clients.put(ORG1,SdkConfig.instance().getOrganisation("peerOrg1").getClient());
        clients.put(ORG2,SdkConfig.instance().getOrganisation("peerOrg2").getClient());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        HFClient client = clients.get(exchange.getPrincipal().getUsername());
        if(client==null){
            exchange.sendResponseHeaders(401,0);
        }

        String path = exchange.getRequestURI().getPath().replaceFirst(".*/([^/?]+).*", "$1");
        log.info("Serving path " + path);
        exchange.getResponseHeaders().add("Content-Type", "application/json, charset=UTF-8");
        exchange.sendResponseHeaders(200, 0);

        OutputStream out = exchange.getResponseBody();
        if (HANDLER_URL.equals(path)) {
            om.writeValue(out,om.readValue(api.getActualList(client), TrackingRecord[].class) );
            log.info("");
        } else if (path.equals(INSERT_PATH)) {
            String text = new Scanner(exchange.getRequestBody()).useDelimiter("\\A").next();
            TrackingRecord trackingRecord = om.readValue(text, TrackingRecord.class);

            // only admin is allowed to write
            try {
                client.setUserContext(SdkConfig.instance().getOrganisation("peerOrg1").getOrgAdmin());
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }

            String trxId = api.insert(client,trackingRecord.getVin(), text);
            log.info("inserted record, trxId {}", trxId);
        } else {
            om.writeValue(out, om.readValue(api.getHistory(client,path), TrackingRecord[].class));
            log.info("Get info about " + path);
        }
        out.close();
    }

}

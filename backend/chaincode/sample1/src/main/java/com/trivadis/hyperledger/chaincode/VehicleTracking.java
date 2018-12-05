package com.trivadis.hyperledger.chaincode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import io.netty.handler.ssl.OpenSsl;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

public class VehicleTracking extends ChaincodeBase {
    private static final Logger log = LoggerFactory.getLogger(VehicleTracking.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            log.info("Init java vehicle tracking chaincode");
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("function other than init is not supported");
            }
            return newSuccessResponse();
        } catch (Exception e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            log.info("Invoke java vehicle tracking chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            if (func.equals("insert")) {
                return insert(stub, params);
            }
            if (func.equals("getActualList")) {
                return getActualList(stub);
            }
            if (func.equals("getHistory")) {
                return getHistory(stub, params);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"insert\", \"getActualList\", \"getHistory\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response insert(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments, expecting %VIN, %JSON");
        }
        String vin = args.get(0);
        String json= args.get(1);


        log.info("insertging for {} value {}",vin,json);

        stub.putStringState(vin,json);

        log.info("successfully inserted {} for {}",json,vin);

        return newSuccessResponse("insert finished successfully", json.getBytes());
    }

    private Response getActualList(ChaincodeStub stub) {

        QueryResultsIterator<KeyValue> range = stub.getStateByRange("VIN0", "VIN999999999");

        List<String> vehicles = new ArrayList<>();
        range.forEach(kv->vehicles.add(kv.getStringValue()));
        String json = "[" + vehicles.stream().collect(Collectors.joining(",")) + "]";
        log.info("actual list {}",json);
        return newSuccessResponse(json,ByteString.copyFrom(json, UTF_8).toByteArray());
    }

    // getHistory callback representing the getHistory of a chaincode
    private Response getHistory(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments, expecting %VIN");
        }
        String vin = args.get(0);

        QueryResultsIterator<KeyModification> result = stub.getHistoryForKey(vin);
        List<String> history = new ArrayList<>();
        result.forEach(km-> history.add(km.getStringValue()));
        String json = "["+history.stream().collect(Collectors.joining(","))+"]";
        log.info("history vin {}, {}",vin,json);
        return newSuccessResponse(json,ByteString.copyFrom(json, UTF_8).toByteArray());
    }

    public static void main(String[] args) {
        System.out.println("OpenSSL avaliable: " + OpenSsl.isAvailable());
        new VehicleTracking().start(args);
    }

}

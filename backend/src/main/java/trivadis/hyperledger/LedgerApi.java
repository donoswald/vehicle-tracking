package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static trivadis.hyperledger.SdkConfig.chaincodeID;

public class LedgerApi {

    private static final Initializer initializer = Initializer.instance();

    private static final Logger log = LoggerFactory.getLogger(LedgerApi.class);
    private static final SdkConfig sdkConfig = SdkConfig.instance();
    private static LedgerApi INSTANCE;

    private LedgerApi() {
    }

    public String getHistory(HFClient client,String vin) {
        try {
            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            request.setFcn("getHistory");
            request.setArgs(vin);
            request.setChaincodeID(chaincodeID);


            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> responses = initializer.getChannel().queryByChaincode(request, initializer.getChannel().getPeers());
            for (ProposalResponse response : responses) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                } else {
                    log.error("error quering {}", request);
                    throw new RuntimeException("error querying");

                }
            }

            if (successful.isEmpty()) {
                throw new RuntimeException("no responses");
            }
            String message = successful.iterator().next().getMessage();
            log.info("successfully returning {}", message);
            return message;
        } catch (Exception e) {
            log.error("error", e);
            throw new RuntimeException(e);
        }
    }

    public String getActualList(HFClient client) {
        try {


            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            request.setFcn("getActualList");
            request.setChaincodeID(chaincodeID);

            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> responses = initializer.getChannel().queryByChaincode(request, initializer.getChannel().getPeers());
            for (ProposalResponse response : responses) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                } else {
                    log.error("error quering {}", request);
                    throw new RuntimeException("error querying");

                }
            }

            if (successful.isEmpty()) {
                throw new RuntimeException("no responses");
            }
            String message = successful.iterator().next().getMessage();
            log.info("successfully returning {}", message);
            return message;
        } catch (Exception e) {
            log.error("error", e);
            throw new RuntimeException(e);
        }
    }

    public String insert(HFClient client,String vin, String json) {

        try {

            ///////////////
            /// Send transaction proposal to all peers
            TransactionProposalRequest request = client.newTransactionProposalRequest();
            request.setChaincodeID(chaincodeID);
            request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
            request.setFcn("insert");
            request.setProposalWaitTime(sdkConfig.getDeployWaitTime());
            request.setArgs(vin, json);

            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> transactionPropResp = initializer.getChannel().sendTransactionProposal(request, initializer.getChannel().getPeers());
            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                } else {
                    throw new RuntimeException("failed response: " + response);
                }
            }

            return initializer.getChannel().sendTransaction(successful).get(sdkConfig.getInvokeWaitTime(), TimeUnit.SECONDS).getTransactionID();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static LedgerApi instance() {
        if (INSTANCE == null) {
            INSTANCE = new LedgerApi();
        }
        return INSTANCE;
    }


}

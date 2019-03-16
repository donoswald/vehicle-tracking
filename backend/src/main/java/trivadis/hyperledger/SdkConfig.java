package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.ChaincodeID;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SdkConfig {
    private static SdkConfig INSTANCE;

    static final String CHAIN_CODE_NAME = "VEHICLE_TRACKING_CC";
    static final String CHAIN_CODE_VERSION = "1";
    static final String CHAINCODE_PATH = "chaincode";
    static final String CHAIN_CODE_FILEPATH = "sample1";
    static final ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
            .setVersion(CHAIN_CODE_VERSION).build();
    static final String NETWORK_PATH = "network";
    static final String ORG1_USR_BASE_PATH =
            NETWORK_PATH + File.separator +
                    "crypto-config" + File.separator +
                    "peerOrganizations" + File.separator +
                    "org1.example.com" + File.separator +
                    "users" + File.separator +
                    "Admin@org1.example.com" + File.separator +
                    "msp";
    static final String ORG1_USR_ADMIN_PK = ORG1_USR_BASE_PATH + File.separator + "keystore";
    static final String ORG1_USR_ADMIN_CERT = ORG1_USR_BASE_PATH + File.separator + "admincerts";
    static final String ORG2_USR_BASE_PATH =
            NETWORK_PATH + File.separator +
                    "crypto-config" + File.separator +
                    "peerOrganizations" + File.separator +
                    "org2.example.com" + File.separator +
                    "users" + File.separator +
                    "Admin@org2.example.com" + File.separator +
                    "msp";
    static final String ORG2_USR_ADMIN_PK = ORG2_USR_BASE_PATH + File.separator + "keystore";
    static final String ORG2_USR_ADMIN_CERT = ORG2_USR_BASE_PATH + File.separator + "admincerts";
    private static final String PROPBASE = "org.hyperledger.fabric.sdktest.";
    static final String DEPLOYWAITTIME = PROPBASE + "DeployWaitTime";
    static final String INVOKEWAITTIME = PROPBASE + "InvokeWaitTime";
    private static final String INTEGRATIONTESTS_ORG = PROPBASE + "integrationTests.org.";
    private static final Pattern orgPat = Pattern.compile("^" + Pattern.quote(INTEGRATIONTESTS_ORG) + "([^\\.]+)\\.mspid$");

    private final HashMap<String, VtOrg> organisations = new HashMap<>();
    private final Properties sdkProperties = new Properties();


    private SdkConfig() {
        try {
            sdkProperties.load(SdkConfig.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<Object, Object> x : sdkProperties.entrySet()) {
            final String key = x.getKey() + "";
            final String val = x.getValue() + "";

            if (key.startsWith(INTEGRATIONTESTS_ORG)) {

                Matcher match = orgPat.matcher(key);

                if (match.matches() && match.groupCount() == 1) {
                    String orgName = match.group(1).trim();
                    organisations.put(orgName, new VtOrg(orgName, val.trim()));

                }
            }
        }

        for (Map.Entry<String, VtOrg> org : organisations.entrySet()) {
            final VtOrg sampleOrg = org.getValue();
            final String orgName = org.getKey();

            String peerNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".peer_locations");
            String[] ps = peerNames.split("[ \t]*,[ \t]*");
            for (String peer : ps) {
                String[] nl = peer.split("[ \t]*@[ \t]*");
                sampleOrg.addPeerLocation(nl[0], nl[1]);
            }

            final String domainName = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".domname");

            sampleOrg.setDomainName(domainName);

            String ordererNames = sdkProperties.getProperty(INTEGRATIONTESTS_ORG + orgName + ".orderer_locations");
            ps = ordererNames.split("[ \t]*,[ \t]*");
            for (String peer : ps) {
                String[] nl = peer.split("[ \t]*@[ \t]*");
                sampleOrg.addOrdererLocation(nl[0], nl[1]);
            }


            sampleOrg.setCALocation(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + org.getKey() + ".ca_location")));

            sampleOrg.setCAName(sdkProperties.getProperty((INTEGRATIONTESTS_ORG + org.getKey() + ".caName")));

        }
    }

    public static SdkConfig instance() {
        if (INSTANCE == null) {
            INSTANCE = new SdkConfig();
        }
        return INSTANCE;
    }

    long getDeployWaitTime() {
        return Long.parseLong(sdkProperties.getProperty(DEPLOYWAITTIME));
    }

    long getInvokeWaitTime() {
        return Long.parseLong(sdkProperties.getProperty(INVOKEWAITTIME));
    }

    public List<VtOrg> getOrganisations() {
        return new ArrayList<>(organisations.values());
    }

    public VtOrg getOrganisation(String name) {
        return organisations.get(name);
    }

}

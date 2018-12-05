package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hyperledger.fabric.sdk.Channel.TransactionOptions.createTransactionOptions;
import static trivadis.hyperledger.SdkConfig.*;

public class Initializer {

    private static final String CHANNEL_NAME = "foo";

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);
    private static final SdkConfig sdkConfig = SdkConfig.instance();
    private static boolean installed = false;

    private HFClient client = HFClient.createNewInstance();
    private Channel channel = null;

    public Initializer() {
        init();
    }

    private void init() {
        try {


            createUsersAndOrgs();

            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            // use the admin of the first org (org1)
            client.setUserContext(sdkConfig.getOrganisations().get(0).getOrgAdmin());

            channel = constructChannel(CHANNEL_NAME, client);
            if (!installed) {
                install(client, channel);
                instantiate(client, channel);
            }

        } catch (Exception e) {
            log.error("error", e);
            throw new RuntimeException(e);
        }
    }

    private static void instantiate(HFClient client, Channel channel) {

        try {


            final ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
                    .setVersion(CHAIN_CODE_VERSION).build();

            ///////////////
            //// Instantiate chaincode.
            InstantiateProposalRequest request = client.newInstantiationProposalRequest();
            request.setProposalWaitTime(sdkConfig.getDeployWaitTime());
            request.setChaincodeID(chaincodeID);
            request.setChaincodeLanguage(TransactionRequest.Type.JAVA);
            request.setFcn("init");
            request.setArgs(new String[0]);

            ChaincodeEndorsementPolicy policy = new ChaincodeEndorsementPolicy();
            policy.fromYamlFile(new File(NETWORK_PATH + "/chaincodeendorsementpolicy.yaml"));
            request.setChaincodeEndorsementPolicy(policy);

            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> responses = channel.sendInstantiationProposal(request, channel.getPeers());
            for (ProposalResponse response : responses) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                } else {
                    log.error("unsuccessful respons {}", response);
                    throw new RuntimeException("error instantiating chaincode: " + response);
                }
            }

            ///////////////
            /// Send instantiate transaction to orderer
            BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(successful, createTransactionOptions() //Basically the default options but shows it's usage.
                    .userContext(client.getUserContext()) //could be a different user context. this is the default.
                    .shuffleOrders(false) // don't shuffle any orderers the default is true.
                    .orderers(channel.getOrderers()) // specify the orderers we want to try this transaction. Fails once all Orderers are tried.
            ).get();

            log.info("successfully instantiated chaincode {}", transactionEvent);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void install(HFClient client, Channel channel) {
        try {
            ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION).build();

            InstallProposalRequest request = client.newInstallProposalRequest();
            request.setChaincodeID(chaincodeID);
            request.setChaincodeSourceLocation(Paths.get(CHAINCODE_PATH, CHAIN_CODE_FILEPATH).toFile());
            request.setChaincodeVersion(CHAIN_CODE_VERSION);
            request.setChaincodeLanguage(TransactionRequest.Type.JAVA);

            Collection<Peer> peers = channel.getPeers();

            Collection<ProposalResponse> responses = client.sendInstallProposal(request, peers);

            for (ProposalResponse response : responses) {
                if (response.getStatus() != ProposalResponse.Status.SUCCESS) {
                    throw new RuntimeException("error installing chaincode: " + response.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private static Channel constructChannel(String name, HFClient client) {

        VtOrg org = sdkConfig.getOrganisation("peerOrg1");
        try {

            //Only peer Admin org
            User peerAdmin = org.getOrgAdmin();
            client.setUserContext(peerAdmin);

            Collection<Orderer> orderers = new LinkedList<>();

            for (String orderName : org.getOrdererNames()) {

                Properties ordererProperties = new Properties();

                //example of setting keepAlive to avoid timeouts on inactive http2 connections.
                // Under 5 minutes would require changes to server side to accept faster ping rates.
                ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
                ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
                ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{true});

                orderers.add(client.newOrderer(orderName, org.getOrdererLocation(orderName),
                        ordererProperties));
            }

            String path = NETWORK_PATH + File.separator + name + ".tx";
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(path));


            File channelFile = new File(CHANNEL_NAME + ".ser");
            Channel channel = null;
            if (!channelFile.exists()) {
                channel = client.newChannel(name, orderers.iterator().next(), channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, peerAdmin));


                for (String peerName : org.getPeerNames()) {
                    String peerLocation = org.getPeerLocation(peerName);

                    Properties peerProperties = new Properties();

                    //Example of setting specific options on grpc's NettyChannelBuilder
                    peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

                    Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
                    channel.joinPeer(peer);

                }

                for (String eventHubName : org.getEventHubNames()) {

                    final Properties eventHubProperties = new Properties();

                    eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
                    eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});

                    EventHub eventHub = client.newEventHub(eventHubName, org.getEventHubLocation(eventHubName),
                            eventHubProperties);
                    channel.addEventHub(eventHub);
                }
            } else {
                installed = true;
                channel = client.newChannel(CHANNEL_NAME);
                for (String peerName : org.getPeerNames()) {
                    Peer peer = client.newPeer(peerName, org.getPeerLocation(peerName));
                    channel.addPeer(peer);
                }
                channel.addOrderer(orderers.iterator().next());
            }

            channel.serializeChannel(channelFile);
            return channel.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void createUsersAndOrgs() {
        try {

            Collection<VtOrg> organisations = sdkConfig.getOrganisations();
            for (VtOrg org : organisations) {
                String caName = org.getCAName();
                HFCAClient ca = null;
                if (caName != null && !caName.isEmpty()) {
                    ca = HFCAClient.createNewInstance(caName, org.getCALocation(), org.getCAProperties());
                } else {
                    ca = HFCAClient.createNewInstance(org.getCALocation(), org.getCAProperties());
                }
                ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

                VtUser admin = new VtUser("admin");
                admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                admin.setMspId(org.getMSPID());
                org.setCaAdmin(admin);

                Collection<HFCAIdentity> identities = ca.getHFCAIdentities(admin);

                if (identities.stream().noneMatch(i -> "user".equals(i.getEnrollmentId()))) {
                    VtUser user = new VtUser("user");
                    RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");
                    rr.setSecret("secret");
                    user.setEnrollmentSecret(ca.register(rr, admin));
                    user.setAffiliation(rr.getAffiliation());
                    user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
                    user.setMspId(org.getMSPID());
                    org.setUser(user);
                } else {
                    HFCAIdentity identity = identities.stream().filter(i -> "user".equals(i.getEnrollmentId())).findAny().get();
                    VtUser user = new VtUser(identity);
                    user.setMspId(org.getMSPID());
                    user.setEnrollment(ca.enroll(user.getName(), "secret"));
                    org.setUser(user);
                }

                if (org.getMSPID().equals("Org1MSP")) {
                    File[] pkFiles = new File(ORG1_USR_ADMIN_PK).listFiles();
                    File[] certFiles = new File(ORG1_USR_ADMIN_CERT).listFiles();
                    Enrollment enrollment = VtOrg.getEnrollment(pkFiles[0], certFiles[0]);

                    VtUser orgAdmin = new VtUser("admin");
                    orgAdmin.setEnrollment(enrollment);
                    orgAdmin.setMspId(org.getMSPID());
                    org.setOrgAdmin(orgAdmin);
                } else if (org.getMSPID().equals("Org2MSP")) {
                    File[] pkFiles = new File(ORG2_USR_ADMIN_PK).listFiles();
                    File[] certFiles = new File(ORG2_USR_ADMIN_CERT).listFiles();
                    Enrollment enrollment = VtOrg.getEnrollment(pkFiles[0], certFiles[0]);

                    VtUser orgAdmin = new VtUser("admin");
                    orgAdmin.setEnrollment(enrollment);
                    orgAdmin.setMspId(org.getMSPID());
                    org.setOrgAdmin(orgAdmin);

                }
            }

        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }

    public Channel getChannel() {
        return channel;
    }
}

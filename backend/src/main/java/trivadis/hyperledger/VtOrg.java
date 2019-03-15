package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class VtOrg {
    private final String name;
    private final String mspid;
    private HFCAClient caClient;

    private Map<String, String> peerLocations = new HashMap<>();
    private Map<String, String> ordererLocations = new HashMap<>();
    private Map<String, String> eventHubLocations = new HashMap<>();
    private String caLocation;
    private Properties caProperties = null;

    private String domainName;
    private String caName;
    private User caAdmin;
    private User orgAdmin;
    private User user;
    private HFClient client = HFClient.createNewInstance();

    VtOrg(String name, String mspid) {
        this.name = name;
        this.mspid = mspid;
        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static CAEnrollment getEnrollment(File keyFile, File certFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        try (InputStream is = new FileInputStream(keyFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder keyBuilder = new StringBuilder();

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.indexOf("PRIVATE") == -1) {
                    keyBuilder.append(line);
                }
            }


            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");

            return new CAEnrollment(
                    kf.generatePrivate(keySpec),
                    new String(Files.readAllBytes(certFile.toPath())));
        }
    }

    String getCAName() {
        return caName;
    }

    void setCAName(String caName) {
        this.caName = caName;
    }

    String getMSPID() {
        return mspid;
    }

    String getCALocation() {
        return this.caLocation;
    }

    void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    void addPeerLocation(String name, String location) {

        peerLocations.put(name, location);
    }

    void addOrdererLocation(String name, String location) {

        ordererLocations.put(name, location);
    }

    String getPeerLocation(String name) {
        return peerLocations.get(name);

    }

    String getOrdererLocation(String name) {
        return ordererLocations.get(name);

    }

    String getEventHubLocation(String name) {
        return eventHubLocations.get(name);

    }

    Set<String> getPeerNames() {

        return Collections.unmodifiableSet(peerLocations.keySet());
    }

    Set<String> getOrdererNames() {

        return Collections.unmodifiableSet(ordererLocations.keySet());
    }

    Set<String> getEventHubNames() {

        return Collections.unmodifiableSet(eventHubLocations.keySet());
    }

    HFCAClient getCAClient() {

        return caClient;
    }

    void setCAClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    String getName() {
        return name;
    }

    Properties getCAProperties() {
        return caProperties;
    }

    String getDomainName() {
        return domainName;
    }

    void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    User getCaAdmin() {
        return caAdmin;
    }

    void setCaAdmin(User caAdmin) {
        this.caAdmin = caAdmin;
    }

    public User getOrgAdmin() {
        return orgAdmin;
    }

    void setOrgAdmin(User orgAdmin) {
        this.orgAdmin = orgAdmin;
    }

    User getUser() {
        return user;
    }

    void setUser(User user) {
        this.user = user;
        try {
            this.client.setUserContext(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public HFClient getClient() {
        return client;
    }

    static class CAEnrollment implements Enrollment, Serializable {
        private PrivateKey key;
        private String cert;

        CAEnrollment(PrivateKey pkey, String signedPem) {
            this.key = pkey;
            this.cert = signedPem;
        }

        public PrivateKey getKey() {
            return key;
        }

        public String getCert() {
            return cert;
        }

    }
}

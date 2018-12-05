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

    public VtOrg(String name, String mspid) {
        this.name = name;
        this.mspid = mspid;
        try{
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getCAName() {
        return caName;
    }

    public void setCAName(String caName) {
        this.caName = caName;
    }


    public String getMSPID() {
        return mspid;
    }

    public String getCALocation() {
        return this.caLocation;
    }

    public void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    public void addPeerLocation(String name, String location) {

        peerLocations.put(name, location);
    }

    public void addOrdererLocation(String name, String location) {

        ordererLocations.put(name, location);
    }

    public String getPeerLocation(String name) {
        return peerLocations.get(name);

    }

    public String getOrdererLocation(String name) {
        return ordererLocations.get(name);

    }

    public String getEventHubLocation(String name) {
        return eventHubLocations.get(name);

    }

    public Set<String> getPeerNames() {

        return Collections.unmodifiableSet(peerLocations.keySet());
    }

    public Set<String> getOrdererNames() {

        return Collections.unmodifiableSet(ordererLocations.keySet());
    }

    public Set<String> getEventHubNames() {

        return Collections.unmodifiableSet(eventHubLocations.keySet());
    }

    public HFCAClient getCAClient() {

        return caClient;
    }

    public void setCAClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    public String getName() {
        return name;
    }

    public Properties getCAProperties() {
        return caProperties;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public User getCaAdmin() {
        return caAdmin;
    }

    public void setCaAdmin(User caAdmin) {
        this.caAdmin = caAdmin;
    }

    public User getOrgAdmin() {
        return orgAdmin;
    }

    public void setOrgAdmin(User orgAdmin) {
        this.orgAdmin = orgAdmin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        try{
            this.client.setUserContext(user);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public HFClient getClient() {
        return client;
    }

    public static CAEnrollment getEnrollment(File keyFile, File certFile)
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

    static class CAEnrollment implements Enrollment, Serializable {
        private PrivateKey key;
        private String cert;

        public CAEnrollment(PrivateKey pkey, String signedPem) {
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

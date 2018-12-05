package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VtUser implements User {
    private String name;
    private Set<String> roles = new HashSet<>();
    private String account;
    private String affiliation;
    private  String enrollmentSecret;
    private Enrollment enrollment;
    private String mspId;

    public VtUser(String name) {
        this.name=name;
    }

    public VtUser(HFCAIdentity identity) {
        this.name=identity.getEnrollmentId();
        this.affiliation=identity.getAffiliation();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(Set<String> roles) {
        this.roles = new HashSet<>(roles);
    }

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }
}

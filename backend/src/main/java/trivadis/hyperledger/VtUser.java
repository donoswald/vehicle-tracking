package trivadis.hyperledger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class VtUser implements User {
    private String name;
    private Set<String> roles = new HashSet<>();
    private String account;
    private String affiliation;
    private String enrollmentSecret;
    private Enrollment enrollment;
    private String mspId;

    VtUser(String name) {
        this.name = name;
    }

    VtUser(HFCAIdentity identity) {
        this.name = identity.getEnrollmentId();
        this.affiliation = identity.getAffiliation();
    }

    @Override
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    void setRoles(Set<String> roles) {
        this.roles = new HashSet<>(roles);
    }

    @Override
    public String getAccount() {
        return account;
    }

    void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    void setMspId(String mspId) {
        this.mspId = mspId;
    }

    String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }
}

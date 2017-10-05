package gonext.smsapp.contacts;

public class ContactDetails {
    private String phoneNo;
    private String phoneType;

    public String getPhoneType() {
        return this.phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public ContactDetails(String phoneType, String phoneNo) {
        this.phoneType = phoneType;
        this.phoneNo = phoneNo;
    }
}

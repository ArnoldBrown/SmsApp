package gonext.smsapp.contacts;

import java.util.ArrayList;

public class ContactData {
    private ArrayList<ContactDetails> contactDetails;
    private String contact_id;
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact_id() {
        return this.contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public ArrayList<ContactDetails> getContactDetails() {
        return this.contactDetails;
    }

    public void setContactDetails(ArrayList<ContactDetails> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public ContactData(String name, String contact_id, ArrayList<ContactDetails> contactDetails) {
        this.name = name;
        this.contact_id = contact_id;
        this.contactDetails = contactDetails;
    }
}

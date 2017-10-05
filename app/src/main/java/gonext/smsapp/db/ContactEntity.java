package gonext.smsapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ram on 14/09/17.
 */
@DatabaseTable(tableName = "Contacts")
public class ContactEntity {
    @DatabaseField(columnName = "Id",generatedId = true)
    private int id;
    @DatabaseField(columnName = "ContactId")
    private String contactId = "";
    @DatabaseField(columnName = "Name")
    private String name="";
    @DatabaseField(columnName = "Mobile")
    private String mobile = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "id=" + id +
                ", contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}

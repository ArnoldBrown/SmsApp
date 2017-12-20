package gonext.smsapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ram on 18/08/17.
 */
@DatabaseTable(tableName = "Notification")
public class NotificationEntity {
    @DatabaseField(columnName = "Id",generatedId = true)
    private int id;
    @DatabaseField(columnName = "Title")
    private String title;
    @DatabaseField(columnName = "fromNumber")
    private String fromNumber;
    @DatabaseField(columnName = "Message")
    private String message;
    @DatabaseField(columnName = "DateTime")
    private String dateTime;
    @DatabaseField(columnName = "Key")
    private String key;
    @DatabaseField(columnName = "When")
    private String when;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fromNumber='" + fromNumber + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", key='" + key + '\'' +
                ", when='" + when + '\'' +
                '}';
    }
}

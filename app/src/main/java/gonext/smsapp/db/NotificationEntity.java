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
    @DatabaseField(columnName = "PackageName")
    private String packageName;
    @DatabaseField(columnName = "Message")
    private String message;
    @DatabaseField(columnName = "DateTime")
    private String dateTime;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", packageName='" + packageName + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}

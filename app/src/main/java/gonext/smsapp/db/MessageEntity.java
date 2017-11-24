package gonext.smsapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ram on 14/09/17.
 */

@DatabaseTable(tableName = "Messages")
public class MessageEntity {
    @DatabaseField(columnName = "Id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "messageId")
    private String  messageId = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}

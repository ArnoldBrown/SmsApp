package gonext.smsapp.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ram on 06/11/17.
 */
@DatabaseTable(tableName = "Medias")
public class MediaEntity {
    @DatabaseField(columnName = "Id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "Name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MediaEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

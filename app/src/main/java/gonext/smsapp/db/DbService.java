package gonext.smsapp.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.query.Not;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ram on 14/09/17.
 */

public class DbService {
    private Context context;
    private Dao<ContactEntity,String> contactEntityDao;
    private Dao<MessageEntity,String> messageEntityDao;
    private Dao<MediaEntity,String> mediaEntityDao;
    private Dao<NotificationEntity,String> notificationEntityDao;
    public DbService(Context context) {
        this.context = context;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            this.contactEntityDao = databaseHelper.getDao(ContactEntity.class);
            this.messageEntityDao = databaseHelper.getDao(MessageEntity.class);
            notificationEntityDao = databaseHelper.getDao(NotificationEntity.class);
            mediaEntityDao = databaseHelper.getDao(MediaEntity.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveContact(ContactEntity contactEntity){
        try {
            contactEntityDao.create(contactEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<ContactEntity> getContacts(){
        try {
           return contactEntityDao.queryForAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ContactEntity getContact(String contactId){
        try {
            return contactEntityDao.queryBuilder().where().eq("ContactId",contactId).queryForFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveMessage(MessageEntity messageEntity){
        try {
            messageEntityDao.create(messageEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public MessageEntity getMessage(String messageId){
        try {
            return messageEntityDao.queryBuilder().where().eq("messageId",messageId).queryForFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void saveNotification(NotificationEntity notificationEntity){
        try {
            notificationEntityDao.create(notificationEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<NotificationEntity> getAllNotifications(){
        try {
            QueryBuilder<NotificationEntity, String> builder = notificationEntityDao.queryBuilder();
            builder.distinct().limit(10l);
            return notificationEntityDao.query(builder.prepare());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public NotificationEntity getNotification(String when){
        try{
            return notificationEntityDao.queryBuilder().where().eq("When",when).queryForFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void deleteNotification(NotificationEntity notificationEntity){
        try{
            notificationEntityDao.delete(notificationEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public NotificationEntity getNotification(int id){
        try{
            return notificationEntityDao.queryBuilder().where().eq("Id", id).queryForFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveMedia(MediaEntity mediaEntity){
        try{
            mediaEntityDao.create(mediaEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public MediaEntity getMedia(String name){
        try{
            return mediaEntityDao.queryBuilder().where().eq("Name", name).queryForFirst();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

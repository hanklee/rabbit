/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.mongodb;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.MongoManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 *
 * @author hank
 */
public class MongoDAO extends DAO {
    private MongoClient client;

    public MongoDAO(){
        this.client = MongoManager.getInatnce().getClient();
    }
    @Override
    public void update(DBObject obj, String table) throws DBException {

    }

    @Override
    public void delete(DBObject obj, String table) throws DBException {

    }

    @Override
    public void insert(DBObject obj, String table) throws DBException {
        try {
            MongoDatabase db = this.client.getDatabase(obj.getDatasource());
            MongoCollection<Document> docs = db.getCollection(table);
            if (docs == null) {
                db.createCollection(table);
            }
            docs = db.getCollection(table);
            obj.beforeInsert(docs);
            Document doc = Document.parse(obj.toDBJson(table).toString());
            docs.insertOne(doc);

//        System.out.println(doc.getObjectId("_id").toString());
            String _id = doc.getObjectId("_id").toString();
            for (Field field : obj.getClass().getFields()) {
                if (field.getName().equals("_id")
                        && field.getType().equals(String.class)) {
                    field.set(obj, _id);
                }
            }
        } catch (Exception e){
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public DBObject getObject(DBObject obj, String table) throws DBException {
        return null;
    }

    @Override
    public void update(Collection<? extends DBObject> objs, String table_name) throws DBException {

    }

    @Override
    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {

    }

    @Override
    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {

    }

    @Override
    public <T> T execute(DAOHandler<T> daoHandler) throws DBException {
        return null;
    }
}

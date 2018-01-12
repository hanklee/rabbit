/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.mongodb;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.MongoManager;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author hank
 */
public class MongoDAO extends DAO {
    private MongoClient client;

    public MongoDAO() {
        this.client = MongoManager.getInatnce().getClient();
    }

    @Override
    public void update(DBObject obj, String table) throws DBException {
        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
        String value;
        try {
            value = (String) idf.get(obj);
        } catch (IllegalAccessException e) {
            throw new DBException(e.getMessage());
        }
        MongoDatabase db = this.client.getDatabase(obj.getDatasource());
        MongoCollection<Document> docs = db.getCollection(table);
        Document document = Document.parse(obj.toDBJson(table).toString());
        docs.replaceOne(eq("_id", new ObjectId(value))
                , document);
    }

    @Override
    public void delete(DBObject obj, String table) throws DBException {
        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
        String value;
        try {
            value = (String) idf.get(obj);
        } catch (IllegalAccessException e) {
            throw new DBException(e.getMessage());
        }
        MongoDatabase db = this.client.getDatabase(obj.getDatasource());
        MongoCollection<Document> docs = db.getCollection(table);
        docs.deleteOne(eq("_id", new ObjectId(value)));
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

        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public DBObject getObject(DBObject obj, final String table) throws DBException {
        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
        String value;
        try {
            value = (String) idf.get(obj);
        } catch (IllegalAccessException e) {
            throw new DBException(e.getMessage());
        }
        MongoDatabase db = this.client.getDatabase(obj.getDatasource());
        MongoCollection<Document> docs = db.getCollection(table);
        Document myDoc = docs.find(eq("_id", new ObjectId(value))).first();
        if (myDoc != null) {
//            System.out.println(myDoc.toJson());
            DBObject clone = obj.clone();
            clone.JsonToObj(new JSONObject(myDoc.toJson()), table);

            try {
                String _id = myDoc.getObjectId("_id").toString();
                for (Field field : obj.getClass().getFields()) {
                    if (field.getName().equals("_id")
                            && field.getType().equals(String.class)) {
                        field.set(clone, _id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clone;
        }
        return null;
    }

    @Override
    public void update(Collection<? extends DBObject> objs, String table_name) throws DBException {
        for (DBObject obj : objs) {
            update(obj, table_name);
        }
    }

    @Override
    public void insert(Collection<? extends DBObject> objs, String table_name) throws DBException {
        for (DBObject obj : objs) {
            insert(obj, table_name);
        }
    }

    @Override
    public void delete(Collection<? extends DBObject> objs, String table_name) throws DBException {
        for (DBObject obj : objs) {
            delete(obj, table_name);
        }
    }

    @Override
    public <T> T execute(final DAOHandler<T> daoHandler) throws DBException {
        return (new MongoExecute<T>(this.client) {
            @Override
            public T execute(Object con) throws DBException {
                return daoHandler.handle(con);
            }
        }).run();
    }
}

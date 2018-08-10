/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.MongoManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.mongodb.client.model.Filters.eq;

//import org.json.JSONObject;

/**
 * @author hank
 */
public class MongoDAO extends DAO {
    private MongoClient client;
    private final String source;
    public MongoDAO(String source) {
        this.client = MongoManager.getInstance().getClient();
        this.source = source;
    }

    @Override
    public void update(DBObject obj, String table) throws DBException {
        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
        ObjectId objId;
        try {
            Object value = idf.get(obj);
            if (value instanceof ObjectId) {
                objId = (ObjectId) value;
            } else if (value instanceof String) {
                objId = new ObjectId((String) value);
            } else
                throw new DBException("NOT Found Id");
        } catch (IllegalAccessException e) {
            throw new DBException(e.getMessage());
        }
        MongoDatabase db = this.client.getDatabase(source);
        MongoCollection<Document> docs = db.getCollection(table);
        Document document = Document.parse(obj.toDBJson(table).toString());
        docs.replaceOne(eq("_id", objId), document);
    }

    @Override
    public void delete(DBObject obj, String table) throws DBException {
        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
//        Class type = idf.getType();
        ObjectId objId;
        try {
            Object value = idf.get(obj);
            if (value instanceof ObjectId) {
                objId = (ObjectId) value;
            } else if (value instanceof String) {
                objId = new ObjectId((String) value);
            } else
                throw new DBException("NOT Found Id");
        } catch (IllegalAccessException e) {
            throw new DBException(e.getMessage());
        }
        MongoDatabase db = this.client.getDatabase(source);
        MongoCollection<Document> docs = db.getCollection(table);
        docs.deleteOne(eq("_id", objId));
    }

    @Override
    public void insert(DBObject obj, String table) throws DBException {
        try {
            MongoDatabase db = this.client.getDatabase(source);
            MongoCollection<Document> docs = db.getCollection(table);
            if (docs == null) {
                db.createCollection(table);
            }
            docs = db.getCollection(table);
            obj.beforeInsert(this, table,docs);
            Document doc = Document.parse(obj.toDBJson(table).toString());
            docs.insertOne(doc);

//        System.out.println(doc.getObjectId("_id").toString());
            Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
            if (idf != null) {
                if (idf.getType().equals(String.class)) {
                    idf.set(obj, doc.getObjectId("_id").toString());
                } else if (idf.getType().equals(ObjectId.class)) {
                    idf.set(obj, doc.getObjectId("_id"));
                }
            }
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public DBObject getObject(final String table, Object... objs) throws DBException {
        Class objclazz = DBObjectManager.getClassByTable(table);
        if (objclazz == null) {
            throw new DBException("not found table class");
        }
        DBObject obj = null;
        try {
            obj = (DBObject) objclazz.newInstance();
        } catch (Exception e) {
            throw new DBException("wrong table class:" + objclazz.toString());
        }

        Field idf = DBObjectManager.getClazzField(obj.getClass()).get("_id");
        if (idf == null) {
            throw new DBException("NOT Found Id");
        }
        ObjectId objId = new ObjectId((String) objs[0]);
//        try {
//            Object value = idf.get(obj);
//            if (value instanceof ObjectId) {
//                objId = (ObjectId) value;
//            } else if (value instanceof String) {
//                objId = new ObjectId((String) value);
//            } else
//                throw new DBException("NOT Found Id");
//        } catch (IllegalAccessException e) {
//            throw new DBException(e.getMessage());
//        }
        MongoDatabase db = this.client.getDatabase(source);
        MongoCollection<Document> docs = db.getCollection(table);
        Document myDoc = docs.find(eq("_id", objId)).first();
        if (myDoc != null) {
//            System.out.println(myDoc.toJson());
            DBObject clone = obj.cloneTableObj((JSONObject) JSONObject.parse(myDoc.toJson()), table);
//            clone.JsonToObj(new JSONObject(myDoc.toJson()), table);

            try {
                if (idf.getType().equals(String.class)) {
                    idf.set(clone, myDoc.getObjectId("_id").toString());
                } else if (idf.getType().equals(ObjectId.class)) {
                    idf.set(clone, myDoc.getObjectId("_id"));
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
        return daoHandler.handle(this.client);
    }
}

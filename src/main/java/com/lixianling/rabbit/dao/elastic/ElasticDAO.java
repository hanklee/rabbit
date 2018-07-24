/**
 * Create time: 11-Jan-2018
 */
package com.lixianling.rabbit.dao.elastic;

import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAO;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.manager.ElasticManager;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author hank
 */
public class ElasticDAO extends DAO {

    private TransportClient client;

    public ElasticDAO() {
        client = ElasticManager.getInstance().getClient();
    }

    @Override
    public void update(final DBObject obj, final String table) throws DBException {
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
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(obj.getDatasource());
        updateRequest.type(table);
        updateRequest.id(value);
        obj.beforeUpdate(this,table, client);
        try {
            XContentBuilder json = jsonBuilder()
                    .startObject();
            Map<String, Object> data = obj.ObjToMap(obj.getTableName());
            for (String key : data.keySet()) {
                json.field(key, data.get(key));
            }
            updateRequest.doc(json.endObject());
            client.update(updateRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException(e.getMessage());
        }
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
        obj.beforeDelete(this,table, client);
        DeleteResponse response = client.prepareDelete(obj.getDatasource(), table, value).get();
        RestStatus restStatus = response.status();
        if (restStatus.equals(RestStatus.NOT_FOUND)) {
            throw new DBException("NOT Found");
        }

    }

    @Override
    public void insert(final DBObject obj, final String table) throws DBException {

        try {
            obj.beforeInsert(this,table, client);
            IndexResponse response = client.prepareIndex(obj.getDatasource(), table)
                    .setSource(obj.toDBJson(table).toString(), XContentType.JSON)
                    .get();
            String _id = response.getId();
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
    public DBObject getObject(DBObject obj, String table) throws DBException {
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
        GetResponse response = client.prepareGet(obj.getDatasource(), table, value).get();
        Map<String, Object> source = response.getSourceAsMap();
        if (source == null || source.keySet().size() == 0) {
            return null;
        }
        DBObject clone = obj.clone();
        clone.MapToObj(source);
        return clone;
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

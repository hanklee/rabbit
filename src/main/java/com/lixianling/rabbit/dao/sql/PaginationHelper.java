/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 01:00
 */
package com.lixianling.rabbit.dao.sql;

import com.lixianling.rabbit.manager.DBObjectManager;
import com.lixianling.rabbit.dao.Page;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Xianling Li(hanklee)
 * $Id: PaginationHelper.java 39 2016-01-08 12:04:37Z hank $
 */
public class PaginationHelper<E> {
    private static Map<String, Page> page_cache = new ConcurrentHashMap<String, Page>();
    private static Map<String, Set<String>> key_relate = new ConcurrentHashMap<String, Set<String>>();

    // page cache =  md5key - page vlaue
    // key_relate = cache key -> List md5key


    public static void clearAllCache() {
        page_cache.clear();
        key_relate.clear();
    }

    public static synchronized void cleanCache(final String cache_key) {
        Set<String> keys = key_relate.get(cache_key);
        if (keys != null) {
            for (String key : keys) {
                page_cache.remove(key);
//                System.err.println("remove,"+key);
            }
            keys.clear();
        }
    }



    public Page<E> fetchCachePage(
            final QueryRunner qRunner,
            final String sqlFetchRows,
            int pageNo,
            int pageSize,
            final ResultSetHandler<List<E>> objectListHandler,
            final String cache_key,
            final Object... params) {
        String backSQL = sqlFetchRows.substring(sqlFetchRows.toUpperCase().indexOf("FROM"), sqlFetchRows.length());
        backSQL = "SELECT count(*) " + backSQL;
        return fetchCachePage(qRunner, backSQL, sqlFetchRows, pageNo, pageSize, objectListHandler, cache_key, params);
    }

    public Page<E> fetchCachePage(
            final QueryRunner qRunner,
            final String sqlCountRows,
            final String sqlFetchRows,
            int pageNo,
            int pageSize,
            final ResultSetHandler<List<E>> objectListHandler,
            final String cache_key,
            final Object... params) {
        try {

            String tmp = sqlFetchRows;
            for(Object o:params) {
                tmp += o;
            }

            String md5Key = DBObjectManager.md5(tmp);

            Page<E> cachePage = (Page<E>)page_cache.get(md5Key);
            if (cachePage != null) {
                return cachePage;
            }
            // determine how many rows are available
            final int rowCount = ((Long) qRunner.query(sqlCountRows,
                    new ScalarHandler(), params)).intValue();

            // calculate the number of pages
            int pageCount = rowCount / pageSize;
            if (rowCount > pageSize * pageCount || pageCount == 0) {
                pageCount++;
            }

            if (pageNo > pageCount)
                pageNo = pageCount;

            // create the page object
            final Page<E> page = new Page<E>();
            page.setPageNumber(pageNo);
            page.setPagesAvailable(pageCount);
            page.setPageSize(pageSize);
            page.setTotal(rowCount);

            // fetch a single page of results
            // Mysql sql
            final int startRow = (pageNo - 1) * pageSize;
            String mySqlFetch = sqlFetchRows + " LIMIT " + startRow + " , " + pageSize;
            List<E> objects = qRunner.query(
                    mySqlFetch,
                    objectListHandler, params);

            //page.getPageItems().addAll(objects);
            page.setPageItems(objects);
            Set<String> keys = key_relate.get(cache_key);
            if (keys == null) {
                keys = new HashSet<String>();
                key_relate.put(cache_key,keys);
            }
//            System.err.println(md5Key+","+cache_key);
            keys.add(md5Key);
            page_cache.put(md5Key, page);
            return page;
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return new Page<E>();
        }

    }

    /**
     * generate the page data
     *
     * @param qRunner           QueryRunner class
     * @param sqlCountRows      sql count string
     * @param sqlFetchRows      sql fetch string
     * @param pageNo            page number
     * @param pageSize          page size
     * @param objectListHandler Object List handler Object
     * @param params            sql parameter
     * @return page object
     */
    public Page<E> fetchPage(
            final QueryRunner qRunner,
            final String sqlCountRows,
            final String sqlFetchRows,
            int pageNo,
            int pageSize,
            final ResultSetHandler<List<E>> objectListHandler,
            final Object... params) {
        try {

            // determine how many rows are available
            final int rowCount = ((Long) qRunner.query(sqlCountRows,
                    new ScalarHandler(), params)).intValue();

            // calculate the number of pages
            int pageCount = rowCount / pageSize;
            if (rowCount > pageSize * pageCount || pageCount == 0) {
                pageCount++;
            }

            if (pageNo > pageCount)
                pageNo = pageCount;

            // create the page object
            final Page<E> page = new Page<E>();
            page.setPageNumber(pageNo);
            page.setPagesAvailable(pageCount);
            page.setPageSize(pageSize);
            page.setTotal(rowCount);

            // fetch a single page of results
            // Mysql sql
            final int startRow = (pageNo - 1) * pageSize;
            String mySqlFetch = sqlFetchRows + " LIMIT " + startRow + " , " + pageSize;

            List<E> objects = qRunner.query(
                    mySqlFetch,
                    objectListHandler, params);

            //page.getPageItems().addAll(objects);
            page.setPageItems(objects);
            return page;
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return new Page<E>();
        }

    }

    public Page<E> fetchPage(
            final QueryRunner qRunner,
            final String sqlFetchRows,
            int pageNo,
            int pageSize,
            final ResultSetHandler<List<E>> objectListHandler,
            final Object... params) {
        String backSQL = sqlFetchRows.substring(sqlFetchRows.toUpperCase().indexOf("FROM"), sqlFetchRows.length());
        backSQL = "SELECT count(*) " + backSQL;
        return fetchPage(qRunner, backSQL, sqlFetchRows, pageNo, pageSize, objectListHandler, params);
    }
}

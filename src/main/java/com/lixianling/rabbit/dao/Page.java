/**
 * Copyright 2015 The rabbit Project
 * Created Date: 2016-01-07 00:58
 */
package com.lixianling.rabbit.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * simple pagination class page
 *
 * @author Xianling Li(hanklee)
 *         $Id: Page.java 36 2016-01-06 17:24:04Z hank $
 */
public class Page<E> {

    private int pageNumber;
    private int pagesAvailable;
    private int total;
    private int pageSize;
    private List<E> pageItems = new ArrayList<E>();

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPagesAvailable() {
        return pagesAvailable;
    }

    public int getTotal() {
        return total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<E> getPageItems() {
        return pageItems;
    }
}

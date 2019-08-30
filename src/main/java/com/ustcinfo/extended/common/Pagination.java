package com.ustcinfo.extended.common;

import org.springframework.data.domain.Page;

/**
 * @author liu.guangyao@ustcinfo.com
 * @date 2019/08/30
 */
public class Pagination {
    private Integer page;
    private Integer pages;
    private Integer perpage;
    private Long total;
    private String sort;
    private String field;

    public Pagination() {
    }

    public Pagination(Integer page, Integer perpage) {
        this.page = page;
        this.perpage = perpage;
    }

    public Pagination(Integer page, Integer perpage, String sort, String field) {
        this.page = page;
        this.perpage = perpage;
        this.sort = sort;
        this.field = field;
    }

    public Pagination mixOfPage(Page<?> page) {
        this.setTotal(page.getTotalElements());
        this.setPages(page.getTotalPages());
        return this;
    }

    public Integer start() {
        return (page - 1) * perpage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPerpage() {
        return perpage;
    }

    public void setPerpage(Integer perpage) {
        this.perpage = perpage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
package com.damai.learning.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 通用分页响应。
 *
 * @param <T> 分页记录类型
 */
@Data
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页码。 */
    private long pageNum;

    /** 每页记录数。 */
    private long pageSize;

    /** 总记录数。 */
    private long total;

    /** 总页数。 */
    private long pages;

    /** 当前页记录。 */
    private List<T> records;
}

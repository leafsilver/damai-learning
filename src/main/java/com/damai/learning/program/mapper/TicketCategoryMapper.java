package com.damai.learning.program.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.program.entity.TicketCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 票档数据访问接口。
 */
@Mapper
public interface TicketCategoryMapper extends BaseMapper<TicketCategory> {
}
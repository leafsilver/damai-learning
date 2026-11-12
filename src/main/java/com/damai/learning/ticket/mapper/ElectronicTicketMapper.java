package com.damai.learning.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damai.learning.ticket.entity.ElectronicTicket;
import com.damai.learning.ticket.vo.ElectronicTicketVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 电子票数据访问接口。
 */
@Mapper
public interface ElectronicTicketMapper
        extends BaseMapper<ElectronicTicket> {

    /**
     * 分页查询指定用户的电子票。
     */
    Page<ElectronicTicketVO> selectUserTicketPage(
            Page<ElectronicTicketVO> page,
            @Param("userId") Long userId
    );
}
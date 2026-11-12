package com.damai.learning.ticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 电子票实体，对应d_electronic_ticket。
 */
@Data
@TableName("d_electronic_ticket")
public class ElectronicTicket {

    /** 主键id。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对外展示的电子票号。 */
    private String ticketNo;

    /** 所属订单号。 */
    private String orderNo;

    /** 当前票在订单中的序号。 */
    private Integer ticketIndex;

    /** 购票用户id。 */
    private Long userId;

    /** 节目id。 */
    private Long programId;

    /** 场次id。 */
    private Long sessionId;

    /** 票档id。 */
    private Long ticketCategoryId;

    /** 电子票状态：1有效，2已使用，3已作废。 */
    private Integer ticketStatus;

    /** 验票时间，未使用时为空。 */
    private LocalDateTime verifyTime;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 修改时间。 */
    private LocalDateTime editTime;
}

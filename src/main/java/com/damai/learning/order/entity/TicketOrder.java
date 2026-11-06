package com.damai.learning.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 票务订单实体，对应数据库表d_order。
 */
@Data
@TableName("d_order")
public class TicketOrder {

    /** 订单主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对外展示的唯一订单号。 */
    private String orderNo;

    /** 下单用户id。 */
    private Long userId;

    /** 节目id。 */
    private Long programId;

    /** 场次id。 */
    private Long sessionId;

    /** 票档id。 */
    private Long ticketCategoryId;

    /** 购买数量。 */
    private Integer quantity;

    /** 下单时的票档单价。 */
    private BigDecimal unitPrice;

    /** 订单总金额。 */
    private BigDecimal totalAmount;

    /** 订单状态：1待支付，2已支付，3已取消。 */
    private Integer orderStatus;

    /** 支付截止时间，超过该时间仍未支付则自动取消。 */
    private LocalDateTime expireTime;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 最后修改时间。 */
    private LocalDateTime editTime;

    /** 数据状态：1正常，0删除。 */
    private Integer status;
}
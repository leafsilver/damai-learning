package com.damai.learning.ticket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户电子票响应。
 */
@Data
public class ElectronicTicketVO {

    /** 电子票号。 */
    private String ticketNo;

    /** 订单号。 */
    private String orderNo;

    /** 当前票在订单中的序号。 */
    private Integer ticketIndex;

    /** 节目标题。 */
    private String programTitle;

    /** 演出地点。 */
    private String place;

    /** 场次名称。 */
    private String sessionName;

    /** 演出时间。 */
    private LocalDateTime showTime;

    /** 票档名称。 */
    private String categoryName;

    /** 下单时单价。 */
    private BigDecimal unitPrice;

    /** 电子票状态：1有效，2已使用，3已作废。 */
    private Integer ticketStatus;

    /** 验票时间，未使用时为空。 */
    private LocalDateTime verifyTime;

    /** 出票时间。 */
    private LocalDateTime createTime;
}

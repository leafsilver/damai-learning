package com.damai.learning.program.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 节目场次响应，包含该场次可售票档。
 */
@Data
public class ProgramSessionVO {

    /** 场次id。 */
    private Long id;

    /** 场次名称。 */
    private String sessionName;

    /** 演出时间。 */
    private LocalDateTime showTime;

    /** 开售时间。 */
    private LocalDateTime saleStartTime;

    /** 停售时间。 */
    private LocalDateTime saleEndTime;

    /** 当前场次的票档列表。 */
    private List<TicketCategoryVO> ticketCategories;
}
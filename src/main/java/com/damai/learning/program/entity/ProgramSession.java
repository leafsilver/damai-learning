package com.damai.learning.program.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 节目场次实体，对应数据库表d_program_session。
 */
@Data
@TableName("d_program_session")
public class ProgramSession {

    /** 场次主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属节目id。 */
    private Long programId;

    /** 场次名称。 */
    private String sessionName;

    /** 演出时间。 */
    private LocalDateTime showTime;

    /** 开售时间。 */
    private LocalDateTime saleStartTime;

    /** 停售时间。 */
    private LocalDateTime saleEndTime;

    /** 场次状态：1启用，0停用。 */
    private Integer sessionStatus;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 最后修改时间。 */
    private LocalDateTime editTime;

    /** 数据状态：1正常，0删除。 */
    private Integer status;
}
package com.damai.learning.program.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.program.entity.ProgramSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 节目场次数据访问接口。
 */
@Mapper
public interface ProgramSessionMapper extends BaseMapper<ProgramSession> {
}
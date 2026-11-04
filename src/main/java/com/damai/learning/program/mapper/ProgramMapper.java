package com.damai.learning.program.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.program.entity.Program;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProgramMapper extends BaseMapper<Program> {
}
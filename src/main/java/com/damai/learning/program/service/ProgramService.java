package com.damai.learning.program.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.program.entity.Program;
import com.damai.learning.program.mapper.ProgramMapper;
import com.damai.learning.program.vo.ProgramDetailVO;
import com.damai.learning.program.vo.ProgramListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProgramService {

    @Autowired
    private ProgramMapper programMapper;

    public List<ProgramListVO> listOnSalePrograms() {
        LambdaQueryWrapper<Program> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Program::getProgramStatus, 1)
                .eq(Program::getStatus, 1)
                .orderByDesc(Program::getCreateTime);

        List<Program> programs =
                programMapper.selectList(queryWrapper);

        return programs.stream()
                .map(this::toProgramListVO)
                .toList();
    }

    public ProgramDetailVO getProgramDetail(Long id) {
        Program program = programMapper.selectById(id);

        if (program == null
                || !Objects.equals(program.getStatus(), 1)
                || !Objects.equals(
                program.getProgramStatus(),
                1
        )) {
            throw new BusinessException(
                    CommonErrorCode.PROGRAM_NOT_EXIST
            );
        }

        return toProgramDetailVO(program);
    }

    private ProgramListVO toProgramListVO(Program program) {
        ProgramListVO programListVO = new ProgramListVO();

        programListVO.setId(program.getId());
        programListVO.setTitle(program.getTitle());
        programListVO.setActor(program.getActor());
        programListVO.setPlace(program.getPlace());
        programListVO.setItemPicture(
                program.getItemPicture()
        );

        return programListVO;
    }

    private ProgramDetailVO toProgramDetailVO(
            Program program
    ) {
        ProgramDetailVO programDetailVO =
                new ProgramDetailVO();

        programDetailVO.setId(program.getId());
        programDetailVO.setTitle(program.getTitle());
        programDetailVO.setActor(program.getActor());
        programDetailVO.setPlace(program.getPlace());
        programDetailVO.setItemPicture(
                program.getItemPicture()
        );

        return programDetailVO;
    }
}
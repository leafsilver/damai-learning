package com.damai.learning.program.controller;

import com.damai.learning.common.result.Result;
import com.damai.learning.program.service.ProgramService;
import com.damai.learning.program.vo.ProgramListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "节目接口")
@RestController
@RequestMapping("/api/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @Operation(summary = "查询已上架节目列表")
    @GetMapping("/list")
    public Result<List<ProgramListVO>> list() {
        return Result.success(
                programService.listOnSalePrograms()
        );
    }
}
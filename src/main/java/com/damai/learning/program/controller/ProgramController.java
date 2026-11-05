package com.damai.learning.program.controller;

import com.damai.learning.common.page.PageResult;
import com.damai.learning.common.result.Result;
import com.damai.learning.program.dto.ProgramCreateRequest;
import com.damai.learning.program.dto.ProgramUpdateRequest;
import com.damai.learning.program.service.ProgramService;
import com.damai.learning.program.vo.ProgramDetailVO;
import com.damai.learning.program.vo.ProgramListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "节目接口")
@RestController
@RequestMapping("/api/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @Operation(summary = "分页查询已上架节目")
    @GetMapping("/list")
    public Result<PageResult<ProgramListVO>> list(
            @RequestParam(
                    value = "pageNum",
                    defaultValue = "1"
            )
            long pageNum,

            @RequestParam(
                    value = "pageSize",
                    defaultValue = "10"
            )
            long pageSize
    ) {
        return Result.success(
                programService.pageOnSalePrograms(
                        pageNum,
                        pageSize
                )
        );
    }

    @Operation(summary = "查询节目详情")
    @GetMapping("/detail/{id}")
    public Result<ProgramDetailVO> detail(
            @PathVariable("id") Long id
    ) {
        return Result.success(
                programService.getProgramDetail(id)
        );
    }

    @Operation(summary = "新增节目")
    @PostMapping("/create")
    public Result<Long> create(
            @Valid @RequestBody ProgramCreateRequest request
    ) {
        return Result.success(
                programService.createProgram(request)
        );
    }

    @Operation(summary = "编辑节目")
    @PutMapping("/update/{id}")
    public Result<Void> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProgramUpdateRequest request
    ) {
        programService.updateProgram(id, request);

        return Result.success();
    }

    @Operation(summary = "下架节目")
    @PutMapping("/off-sale/{id}")
    public Result<Void> offSale(
            @PathVariable("id") Long id
    ) {
        programService.offSaleProgram(id);

        return Result.success();
    }
}
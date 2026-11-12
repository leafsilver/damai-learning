package com.damai.learning.program.controller;

import com.damai.learning.common.page.PageResult;
import com.damai.learning.common.result.Result;
import com.damai.learning.program.dto.ProgramCreateRequest;
import com.damai.learning.program.dto.ProgramUpdateRequest;
import com.damai.learning.program.service.ProgramSearchService;
import com.damai.learning.program.service.ProgramService;
import com.damai.learning.program.vo.ProgramDetailVO;
import com.damai.learning.program.vo.ProgramListVO;
import com.damai.learning.program.vo.ProgramSearchVO;
import com.damai.learning.program.vo.ProgramSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 节目管理接口。
 */
@Tag(name = "节目接口")
@RestController
@RequestMapping("/api/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramSearchService programSearchService;



    @Operation(summary = "分页查询已上架节目")
    @GetMapping("/list")
    public Result<PageResult<ProgramListVO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize
    ) {
        return Result.success(programService.pageOnSalePrograms(pageNum, pageSize));
    }

    @Operation(summary = "查询节目详情")
    @GetMapping("/detail/{id}")
    public Result<ProgramDetailVO> detail(@PathVariable("id") Long id) {
        return Result.success(programService.getProgramDetail(id));
    }

    @Operation(summary = "新增节目")
    @PostMapping("/create")
    public Result<Long> create(@Valid @RequestBody ProgramCreateRequest request) {
        return Result.success(programService.createProgram(request));
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
    public Result<Void> offSale(@PathVariable("id") Long id) {
        programService.offSaleProgram(id);

        return Result.success();
    }

    @Operation(summary = "查询节目场次和票档")
    @GetMapping("/{programId}/sessions")
    public Result<List<ProgramSessionVO>> sessions(@PathVariable("programId") Long programId) {
        return Result.success(programService.listProgramSessions(programId));
    }

    @Operation(summary = "重建节目搜索索引")
    @PostMapping("/search/rebuild")
    public Result<Integer> rebuildSearchIndex() {
        return Result.success(
                programSearchService.rebuildProgramIndex()
        );
    }

    @Operation(summary = "搜索已上架节目")
    @GetMapping("/search")
    public Result<PageResult<ProgramSearchVO>> search(
            @RequestParam("keyword") String keyword,
            @RequestParam(
                    value = "pageNum",
                    defaultValue = "1"
            ) long pageNum,
            @RequestParam(
                    value = "pageSize",
                    defaultValue = "10"
            ) long pageSize
    ) {
        return Result.success(
                programSearchService.searchPrograms(
                        keyword,
                        pageNum,
                        pageSize
                )
        );
    }
}

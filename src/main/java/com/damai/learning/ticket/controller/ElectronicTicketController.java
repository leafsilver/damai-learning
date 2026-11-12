package com.damai.learning.ticket.controller;

import com.damai.learning.common.page.PageResult;
import com.damai.learning.common.result.Result;
import com.damai.learning.ticket.service.ElectronicTicketService;
import com.damai.learning.ticket.vo.ElectronicTicketVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 电子票查询接口。
 */
@Tag(name = "电子票接口")
@RestController
@RequestMapping("/api/ticket")
public class ElectronicTicketController {

    @Autowired
    private ElectronicTicketService electronicTicketService;

    @Operation(summary = "分页查询用户电子票")
    @GetMapping("/list")
    public Result<PageResult<ElectronicTicketVO>> list(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize
    ) {
        return Result.success(
                electronicTicketService.pageUserTickets(
                        userId,
                        pageNum,
                        pageSize
                )
        );
    }

    @Operation(summary = "核销电子票")
    @PutMapping("/verify/{ticketNo}")
    public Result<Void> verify(@PathVariable("ticketNo") String ticketNo) {
        electronicTicketService.verifyTicket(ticketNo);

        return Result.success();
    }
}

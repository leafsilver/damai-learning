package com.damai.learning.ticket.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.page.PageResult;
import com.damai.learning.order.entity.TicketOrder;
import com.damai.learning.order.enums.OrderStatus;
import com.damai.learning.order.mapper.TicketOrderMapper;
import com.damai.learning.ticket.entity.ElectronicTicket;
import com.damai.learning.ticket.enums.ElectronicTicketStatus;
import com.damai.learning.ticket.mapper.ElectronicTicketMapper;
import com.damai.learning.ticket.vo.ElectronicTicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 电子票业务服务。
 */
@Service
public class ElectronicTicketService {

    @Autowired
    private TicketOrderMapper ticketOrderMapper;

    @Autowired
    private ElectronicTicketMapper electronicTicketMapper;

    /**
     * 根据已支付订单生成电子票。
     */
    public void issueTickets(String orderNo) {
        TicketOrder ticketOrder = findPaidOrder(orderNo);

        long issuedCount = countIssuedTickets(orderNo);

        if (issuedCount == ticketOrder.getQuantity()) {
            return;
        }

        if (issuedCount != 0) {
            throw new IllegalStateException(
                    "订单电子票数量异常，orderNo=" + orderNo
            );
        }

        for (int ticketIndex = 1;
             ticketIndex <= ticketOrder.getQuantity();
             ticketIndex++) {
            ElectronicTicket electronicTicket = buildElectronicTicket(
                    ticketOrder,
                    ticketIndex
            );

            int insertedRows =
                    electronicTicketMapper.insert(electronicTicket);

            if (insertedRows != 1) {
                throw new IllegalStateException(
                        "电子票生成失败，orderNo=" + orderNo
                );
            }
        }
    }

    /**
     * 分页查询指定用户的电子票。
     */
    public PageResult<ElectronicTicketVO> pageUserTickets(
            Long userId,
            long pageNum,
            long pageSize
    ) {
        if (userId == null
                || userId <= 0
                || pageNum < 1
                || pageSize < 1
                || pageSize > 100) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_PARAMETER
            );
        }

        Page<ElectronicTicketVO> page =
                new Page<>(pageNum, pageSize);

        Page<ElectronicTicketVO> ticketPage =
                electronicTicketMapper.selectUserTicketPage(
                        page,
                        userId
                );

        return new PageResult<>(
                ticketPage.getCurrent(),
                ticketPage.getSize(),
                ticketPage.getTotal(),
                ticketPage.getPages(),
                ticketPage.getRecords()
        );
    }

    /**
     * 核销一张有效电子票。
     */
    public void verifyTicket(String ticketNo) {
        if (ticketNo == null || ticketNo.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        ElectronicTicket electronicTicket = findTicketByTicketNo(ticketNo);
        Integer currentStatus = electronicTicket.getTicketStatus();

        if (Objects.equals(currentStatus, ElectronicTicketStatus.USED.getCode())) {
            throw new BusinessException(CommonErrorCode.ELECTRONIC_TICKET_ALREADY_USED);
        }

        if (!Objects.equals(currentStatus, ElectronicTicketStatus.VALID.getCode())) {
            throw new BusinessException(CommonErrorCode.ELECTRONIC_TICKET_INVALID);
        }

        LambdaUpdateWrapper<ElectronicTicket> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(ElectronicTicket::getId, electronicTicket.getId())
                .eq(ElectronicTicket::getTicketStatus, ElectronicTicketStatus.VALID.getCode())
                .set(ElectronicTicket::getTicketStatus, ElectronicTicketStatus.USED.getCode())
                .set(ElectronicTicket::getVerifyTime, LocalDateTime.now());

        int updatedRows = electronicTicketMapper.update(null, updateWrapper);

        if (updatedRows != 1) {
            // 查询后可能被另一个检票口抢先核销。
            throw new BusinessException(CommonErrorCode.ELECTRONIC_TICKET_ALREADY_USED);
        }
    }

    /**
     * 查询有效的已支付订单。
     */
    private TicketOrder findPaidOrder(String orderNo) {
        LambdaQueryWrapper<TicketOrder> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper
                .eq(TicketOrder::getOrderNo, orderNo)
                .eq(TicketOrder::getStatus, 1);

        TicketOrder ticketOrder =
                ticketOrderMapper.selectOne(queryWrapper);

        if (ticketOrder == null) {
            throw new IllegalStateException(
                    "出票订单不存在，orderNo=" + orderNo
            );
        }

        if (!Objects.equals(
                ticketOrder.getOrderStatus(),
                OrderStatus.PAID.getCode()
        )) {
            throw new IllegalStateException(
                    "未支付订单不能出票，orderNo=" + orderNo
            );
        }

        return ticketOrder;
    }

    /**
     * 查询订单已经生成的电子票数量。
     */
    private long countIssuedTickets(String orderNo) {
        LambdaQueryWrapper<ElectronicTicket> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper.eq(
                ElectronicTicket::getOrderNo,
                orderNo
        );

        return electronicTicketMapper.selectCount(queryWrapper);
    }

    /**
     * 根据票号查询电子票。
     */
    private ElectronicTicket findTicketByTicketNo(String ticketNo) {
        LambdaQueryWrapper<ElectronicTicket> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ElectronicTicket::getTicketNo, ticketNo);

        ElectronicTicket electronicTicket = electronicTicketMapper.selectOne(queryWrapper);

        if (electronicTicket == null) {
            throw new BusinessException(CommonErrorCode.ELECTRONIC_TICKET_NOT_EXIST);
        }

        return electronicTicket;
    }

    /**
     * 组装电子票实体。
     */
    private ElectronicTicket buildElectronicTicket(
            TicketOrder ticketOrder,
            int ticketIndex
    ) {
        ElectronicTicket electronicTicket =
                new ElectronicTicket();

        electronicTicket.setTicketNo(generateTicketNo());
        electronicTicket.setOrderNo(ticketOrder.getOrderNo());
        electronicTicket.setTicketIndex(ticketIndex);
        electronicTicket.setUserId(ticketOrder.getUserId());
        electronicTicket.setProgramId(ticketOrder.getProgramId());
        electronicTicket.setSessionId(ticketOrder.getSessionId());
        electronicTicket.setTicketCategoryId(
                ticketOrder.getTicketCategoryId()
        );
        electronicTicket.setTicketStatus(
                ElectronicTicketStatus.VALID.getCode()
        );

        return electronicTicket;
    }

    /**
     * 生成电子票号。
     */
    private String generateTicketNo() {
        return "ET"
                + UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}

package com.damai.learning.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.order.dto.OrderCancelRequest;
import com.damai.learning.order.dto.OrderCreateRequest;
import com.damai.learning.order.entity.TicketOrder;
import com.damai.learning.order.enums.OrderStatus;
import com.damai.learning.order.mapper.TicketOrderMapper;
import com.damai.learning.order.vo.OrderCreateVO;
import com.damai.learning.program.entity.Program;
import com.damai.learning.program.entity.ProgramSession;
import com.damai.learning.program.entity.TicketCategory;
import com.damai.learning.program.mapper.ProgramMapper;
import com.damai.learning.program.mapper.ProgramSessionMapper;
import com.damai.learning.program.mapper.TicketCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 订单业务服务，负责下单校验、库存扣减和订单创建。
 */
@Service
public class OrderService {

    @Autowired
    private TicketOrderMapper ticketOrderMapper;

    @Autowired
    private TicketCategoryMapper ticketCategoryMapper;

    @Autowired
    private ProgramSessionMapper programSessionMapper;

    @Autowired
    private ProgramMapper programMapper;

    /**
     * 创建待支付订单。
     */
    @Transactional
    public OrderCreateVO createOrder(OrderCreateRequest request) {
        TicketCategory ticketCategory = ticketCategoryMapper.selectById(request.getTicketCategoryId());

        validateTicketCategory(ticketCategory);

        ProgramSession programSession = programSessionMapper.selectById(ticketCategory.getSessionId());

        validateProgramSession(programSession);
        validateSaleTime(programSession);

        Program program = programMapper.selectById(programSession.getProgramId());
        validateProgram(program);

        int affectedRows = ticketCategoryMapper.deductStock(ticketCategory.getId(), request.getQuantity());

        if (affectedRows != 1) {
            throw new BusinessException(CommonErrorCode.TICKET_STOCK_NOT_ENOUGH);
        }

        TicketOrder ticketOrder = buildTicketOrder(request, ticketCategory, programSession);

        int insertedRows = ticketOrderMapper.insert(ticketOrder);

        if (insertedRows != 1) {
            throw new BusinessException(CommonErrorCode.ORDER_CREATE_FAILED);
        }

        return toOrderCreateVO(ticketOrder);
    }

    /**
     * 取消待支付订单并恢复库存。
     */
    @Transactional
    public void cancelOrder(OrderCancelRequest request) {
        TicketOrder ticketOrder = getUserOrder(request.getUserId(), request.getOrderNo());
        Integer currentStatus = ticketOrder.getOrderStatus();

        if (Objects.equals(currentStatus, OrderStatus.CANCELED.getCode())) {
            return;
        }

        if (!Objects.equals(currentStatus, OrderStatus.PENDING_PAYMENT.getCode())) {
            throw new BusinessException(CommonErrorCode.ORDER_STATUS_NOT_ALLOWED);
        }

        LambdaUpdateWrapper<TicketOrder> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(TicketOrder::getId, ticketOrder.getId())
                .eq(TicketOrder::getUserId, request.getUserId())
                .eq(TicketOrder::getOrderStatus, OrderStatus.PENDING_PAYMENT.getCode())
                .eq(TicketOrder::getStatus, 1)
                .set(TicketOrder::getOrderStatus, OrderStatus.CANCELED.getCode());

        int updatedRows = ticketOrderMapper.update(null, updateWrapper);

        if (updatedRows != 1) {
            throw new BusinessException(CommonErrorCode.ORDER_CANCEL_FAILED);
        }

        int restoredRows = ticketCategoryMapper.restoreStock(
                ticketOrder.getTicketCategoryId(), ticketOrder.getQuantity());

        if (restoredRows != 1) {
            throw new BusinessException(CommonErrorCode.ORDER_CANCEL_FAILED);
        }
    }

    /**
     * 校验票档存在且处于可售状态。
     */
    private void validateTicketCategory(TicketCategory ticketCategory) {
        if (ticketCategory == null
                || !Objects.equals(ticketCategory.getTicketStatus(), 1)
                || !Objects.equals(ticketCategory.getStatus(), 1)) {
            throw new BusinessException(CommonErrorCode.TICKET_CATEGORY_NOT_AVAILABLE);
        }
    }

    /**
     * 校验场次存在且处于启用状态。
     */
    private void validateProgramSession(ProgramSession programSession) {
        if (programSession == null
                || !Objects.equals(programSession.getSessionStatus(), 1)
                || !Objects.equals(programSession.getStatus(), 1)) {
            throw new BusinessException(CommonErrorCode.PROGRAM_SESSION_NOT_AVAILABLE);
        }
    }

    /**
     * 校验当前时间处于场次售票时间范围内。
     */
    private void validateSaleTime(ProgramSession programSession) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(programSession.getSaleStartTime())
                || now.isAfter(programSession.getSaleEndTime())) {
            throw new BusinessException(CommonErrorCode.PROGRAM_SESSION_NOT_ON_SALE);
        }
    }

    /**
     * 校验所属节目存在且已经上架。
     */
    private void validateProgram(Program program) {
        if (program == null
                || !Objects.equals(program.getProgramStatus(), 1)
                || !Objects.equals(program.getStatus(), 1)) {
            throw new BusinessException(CommonErrorCode.PROGRAM_NOT_EXIST);
        }
    }

    /**
     * 组装待写入数据库的订单实体。
     */
    private TicketOrder buildTicketOrder(OrderCreateRequest request, TicketCategory ticketCategory,
                                         ProgramSession programSession) {
        BigDecimal totalAmount = ticketCategory.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        TicketOrder ticketOrder = new TicketOrder();

        ticketOrder.setOrderNo(generateOrderNo());
        ticketOrder.setUserId(request.getUserId());
        ticketOrder.setProgramId(programSession.getProgramId());
        ticketOrder.setSessionId(programSession.getId());
        ticketOrder.setTicketCategoryId(ticketCategory.getId());
        ticketOrder.setQuantity(request.getQuantity());
        ticketOrder.setUnitPrice(ticketCategory.getPrice());
        ticketOrder.setTotalAmount(totalAmount);
        ticketOrder.setOrderStatus(OrderStatus.PENDING_PAYMENT.getCode());
        ticketOrder.setStatus(1);

        return ticketOrder;
    }

    /**
     * 生成当前项目使用的订单号。
     */
    private String generateOrderNo() {
        return "DM" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 将订单实体转换成创建订单响应。
     */
    private OrderCreateVO toOrderCreateVO(TicketOrder ticketOrder) {
        OrderCreateVO orderCreateVO = new OrderCreateVO();

        orderCreateVO.setOrderId(ticketOrder.getId());
        orderCreateVO.setOrderNo(ticketOrder.getOrderNo());
        orderCreateVO.setTotalAmount(ticketOrder.getTotalAmount());
        orderCreateVO.setOrderStatus(ticketOrder.getOrderStatus());

        return orderCreateVO;
    }

    /**
     * 查询指定用户的有效订单。
     */
    private TicketOrder getUserOrder(Long userId, String orderNo) {
        LambdaQueryWrapper<TicketOrder> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(TicketOrder::getUserId, userId)
                .eq(TicketOrder::getOrderNo, orderNo)
                .eq(TicketOrder::getStatus, 1);

        TicketOrder ticketOrder = ticketOrderMapper.selectOne(queryWrapper);

        if (ticketOrder == null) {
            throw new BusinessException(CommonErrorCode.ORDER_NOT_EXIST);
        }

        return ticketOrder;
    }
}

package com.example.service;

import com.example.dto.RedPacketDto;

import java.math.BigDecimal;

/**
 * 红包业务逻辑处理接口
 */
public interface IRedPacketService {

    /**
     * 发红包业务逻辑
     * @param dto
     * @return
     */
    String handOut(RedPacketDto dto) throws Exception;

    /**
     * 抢红包业务逻辑
     * @param userId
     * @param redId
     * @return
     * @throws Exception
     */
    BigDecimal rob(Integer userId, String redId) throws Exception;
}

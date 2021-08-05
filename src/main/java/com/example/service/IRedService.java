package com.example.service;

import com.example.dto.RedPacketDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包业务逻辑处理过程数据记录接口
 * 异步实现
 */
public interface IRedService {

    /**
     * 发红包时红包的唯一标识、随机金额列表、个数等信息记录到数据库
     * @param dto
     * @param redId
     * @param amountList
     */
    void recordRedPacket(RedPacketDto dto, String redId, List<Integer> amountList) throws Exception;

    /**
     * 抢红包时用户强到的红包金额、用户ID、红包ID等信息录入数据库
     * @param userId
     * @param redId
     * @param amount
     */
    void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) throws Exception;
}

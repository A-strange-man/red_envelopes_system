package com.example.service.impl;

import com.example.dto.RedPacketDto;
import com.example.entity.RedDetail;
import com.example.entity.RedRecord;
import com.example.entity.RedRobRecord;
import com.example.mapper.RedDetailMapper;
import com.example.mapper.RedRecordMapper;
import com.example.mapper.RedRobRecordMapper;
import com.example.service.IRedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 红包业务逻辑处理过程数据记录接口 - 实现类
 */
@Service
@EnableAsync
public class IRedServiceImpl implements IRedService {

    // 日志工具
    private static final Logger log = LoggerFactory.getLogger(IRedServiceImpl.class);

    // 发红包时 红包唯一标识等信息操作接口mapper
    @Autowired
    private RedRecordMapper redRecordMapper;

    // 发红包时 红包随机金额列表等信息操作接口mapper
    @Autowired
    private RedDetailMapper redDetailMapper;

    // 抢红包时相关数据接口
    @Autowired
    private RedRobRecordMapper redRobRecordMapper;

    /**
     * 发红包的信息异步记录到数据库
     * @param dto
     * @param redId
     * @param amountList
     */
    @Override
    @Async("executor")
    @Transactional(rollbackFor = Exception.class)
    public void recordRedPacket(RedPacketDto dto, String redId, List<Integer> amountList) throws Exception{
        // 红包信息对象
        RedRecord redRecord = new RedRecord();
        redRecord.setUserId(dto.getUserId());
        redRecord.setRedPacket(redId);
        redRecord.setTotal(dto.getTotalPeopleNum());
        redRecord.setAmount(BigDecimal.valueOf(dto.getTotalAmount()));
        redRecord.setCreateTime(new Date());
        // 红包信息记录到数据库
        redRecordMapper.insert(redRecord);

        // 红包随机金额信息对象
        RedDetail redDetail;
        // 遍历存储每一个小金额信息
        for (Integer i : amountList) {
            redDetail = new RedDetail();
            redDetail.setAmount(BigDecimal.valueOf(i));
            redDetail.setRecordId(redRecord.getId());
            redDetail.setCreateTime(new Date());
            // 存储
            redDetailMapper.insert(redDetail);
        }
    }

    /**
     * 抢红包的信息记录到数据库
     * @param userId
     * @param redId
     * @param amount
     */
    @Override
    @Async("executor")
    public void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) throws Exception{
        // 抢到红包的实体对象
        RedRobRecord redRobRecord = new RedRobRecord();
        redRobRecord.setUserId(userId);
        redRobRecord.setRedPacket(redId);
        redRobRecord.setAmount(amount);
        redRobRecord.setRobTime(new Date());

        // 记录到数据库
        redRobRecordMapper.insert(redRobRecord);
    }
}

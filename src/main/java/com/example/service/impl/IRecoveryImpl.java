package com.example.service.impl;

import com.example.dto.RedPacketInfo;
import com.example.entity.RedRobRecord;
import com.example.mapper.RedRobRecordMapper;
import com.example.service.IRecovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author CaoJing
 * @date 2021/08/05 10:25
 */
@Service
public class IRecoveryImpl implements IRecovery {

    @Autowired
    private RedRobRecordMapper redRobRecordMapper;

    @Override
    public void RedPacketRecovery(RedPacketInfo redPacketInfo) {
        String redId = redPacketInfo.getRedId();
        Integer userId = redPacketInfo.getUserId();
        Integer totalAmount = redPacketInfo.getTotalAmount();

        // 跟新数据库中的红包状态为 非活跃
        // 。。。

        Integer total = redRobRecordMapper.getSumByRedId(redId);
        int recovery = totalAmount - total;

        // 红包被抢完则不回收
        if (recovery == 0) {
            return;
        }

        RedRobRecord redRobRecord = new RedRobRecord();
        redRobRecord.setRobTime(new Date());
        redRobRecord.setRedPacket(redId);
        redRobRecord.setUserId(userId);
        redRobRecord.setAmount(new BigDecimal(recovery));
        redRobRecord.setIsActive(0);
        // 插入数据库，剩余金额被发红包的人抢到
        redRobRecordMapper.insert(redRobRecord);
    }
}

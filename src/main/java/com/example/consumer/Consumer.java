package com.example.consumer;

import com.example.dto.RedPacketInfo;
import com.example.service.IRecovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author CaoJing
 * @date 2021/08/05 10:16
 */
@Component
public class Consumer {
    // 日志工具
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    // 红包回收接口
    @Autowired
    private IRecovery iRecovery;

    @RabbitListener(queues = "receive_queue", containerFactory = "singleListenerContainer")
    public void consumeMsg(RedPacketInfo redPacketInfo) {
        try {
            log.info("基本消息模型-消费者-监听消费到消息：{}, 时间：{}", redPacketInfo.toString(), new Date());

            iRecovery.RedPacketRecovery(redPacketInfo);

            log.info("红包回收结束，" + new Date());

        } catch (Exception e) {
            log.error("基本消息模型-消费者-发生异常：", e.fillInStackTrace());
        }
    }
}

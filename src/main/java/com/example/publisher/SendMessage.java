package com.example.publisher;

import com.example.dto.RedPacketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author CaoJing
 * @date 2021/08/05 9:38
 */
@Component
public class SendMessage {

    @Autowired
    private static final Logger log = LoggerFactory.getLogger(SendMessage.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDelayMessage(RedPacketInfo redPacketInfo) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        log.info("===============延时队列生产消息====================");
        log.info("发送时间：{}, 发送内容：{}", new Date(), redPacketInfo.toString());

        rabbitTemplate.convertAndSend(
                "delay_exchange",
                "delay_key",
                redPacketInfo,
                message -> {
                    message.getMessageProperties().setExpiration("30000");
                    return message;
                }
        );
        log.info("30000ms后执行");
    }
}

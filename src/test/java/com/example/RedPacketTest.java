package com.example;

import com.example.util.RedPacketUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedPacketTest {

    private static final Logger log = LoggerFactory.getLogger(RedPacketTest.class);

    @Test
    public void repTest() {
        // 10元 == 1000分
        Integer totalAmount = 1000;
        Integer totalPeople = 10;
        log.info("总金额={}分，总红包个数={}个", totalAmount, totalPeople );
        // 产生随机金额列表
        List<Integer> list = RedPacketUtil.divideRedPackage(totalAmount, totalPeople);

        // 统计生成的金额和总金额是否相等
        Integer sum = 0;
        for (Integer i : list) {
            log.info("随即金额为：{}分，即{}元", i
                    , new BigDecimal(i.toString()).divide(new BigDecimal(100)));
            sum += i;
        }
        log.info("所有随机金额总和={}分", sum);
    }
}

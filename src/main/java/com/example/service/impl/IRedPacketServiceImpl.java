package com.example.service.impl;

import com.example.service.IRedPacketService;
import com.example.dto.RedPacketDto;
import com.example.dto.RedPacketInfo;
import com.example.publisher.SendMessage;
import com.example.service.IRedService;
import com.example.util.RedPacketUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 红包业务逻辑处理接口 的 实现类
 */
@Service
public class IRedPacketServiceImpl implements IRedPacketService {

    // 定义日志工具
    private static final Logger log = LoggerFactory.getLogger(IRedPacketServiceImpl.class);

    // 存放到缓存时的key前缀
    private static final String keyPrefix = "redis:red:packet";

    // 定义redis操作组件
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 序列化组件
    @Autowired
    private ObjectMapper objectMapper;

    // 红包业务处理过程中数据记录接口
    @Autowired
    private IRedService redService;

    @Autowired
    private SendMessage sendMessage;

    /**
     * 发红包处理
     * @param dto
     * @return
     */
    @Override
    public String handOut(RedPacketDto dto) throws Exception {
        // 参数合法判断
        if (dto.getTotalAmount() > 0 && dto.getTotalPeopleNum() > 0) {

            // redis操作组件
            ListOperations<String, Object> listOperations = redisTemplate.opsForList();

            // 生成红包份额列表
            List<Integer> amountList = RedPacketUtil.divideRedPackage(dto.getTotalAmount(), dto.getTotalPeopleNum());

            // 根据当前时间戳(纳秒级别)生成红包唯一标识
            String timeStamp = String.valueOf(System.nanoTime());

            // 根据keyPrefix拼接用于存储金额列表的key
            String redId = new StringBuffer(keyPrefix)
                    .append(dto.getUserId())
                    .append(timeStamp)
                    .toString();
            // 随机金额列表放入缓存
            for (Integer i : amountList) {
                // 序列化
                String content = objectMapper.writeValueAsString(i);
                listOperations.leftPush(redId, content);
            }
            // 设置子红包过期时间 1min
            redisTemplate.expire(redId, 20, TimeUnit.SECONDS);

            // 拼接一个用于存储红包总数的key
            String redTotalKey = redId + ":total";
            // 红包总数信息放入缓存
            redisTemplate.opsForValue().set(redTotalKey, dto.getTotalPeopleNum(), 20, TimeUnit.SECONDS);

            // 用延迟队列回收红包 参数：redPacketInfo
            RedPacketInfo redPacketInfo = new RedPacketInfo();
            redPacketInfo.setRedId(redId);
            redPacketInfo.setUserId(dto.getUserId());
            redPacketInfo.setTotalAmount(dto.getTotalAmount());
            // 放入延迟队列
            sendMessage.sendDelayMessage(redPacketInfo);

            // 异步记录红包信息
            redService.recordRedPacket(dto, redId, amountList);
            return redId;

        }else {
            throw new Exception("系统异常-分发红包-参数不合法");
        }
    }

    /**
     * 抢红包处理
     * @param userId 用户id
     * @param redId 用户要抢的红包的标识
     * @return 用户抢到的金额 (元)
     * @throws Exception
     */
    @Override
    public BigDecimal rob(Integer userId, String redId) throws Exception {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // 判断用户是否已经抢过该红包，如果抢过，则显示红包金额
        Object obj = ops.get(redId + userId + ":rob");
        if (obj != null) {
            return new BigDecimal(obj.toString());
        }

        Boolean res = click(redId);
        if (res) {
            // 加分布式锁- 一个人只能抢到最多一个红包, 锁过期时间为24h
            final String lockKey = redId + userId + "-lock";
            Boolean lock = ops.setIfAbsent(lockKey, redId);
            redisTemplate.expire(lockKey, 24L, TimeUnit.HOURS);
            try {
                if (lock) {
                    // 当前线程获取到了该分布式锁，用户之前没有抢过红包
                    Object val = redisTemplate.opsForList().rightPop(redId);
                    if (val != null) {
                        // 红包金额反序列化
                        Integer value = objectMapper.readValue(val.toString(), Integer.class);
                        // 用户抢到了红包, 更新红包个数（-1）
                        String redTotalKey = redId + ":total";
                        Integer currTotal = (ops.get(redTotalKey) != null) ? Integer.parseInt(ops.get(redTotalKey).toString()) : 0;
                        ops.set(redTotalKey, currTotal - 1);
                        // 抢到红包金额记录到数据库
                        redService.recordRobRedPacket(userId, redId, new BigDecimal(value));

                        // 抢到的钱结果返回到前端，单位转换为 ’元‘
                        BigDecimal result = new BigDecimal(value).divide(new BigDecimal(1000));
                        // 将当前抢到红包的用户信息放到缓存
                        ops.set(redId + userId + ":rob", result, 24L, TimeUnit.HOURS);
                        // 打印抢到红包的信息日志
                        log.info("当前用户抢到红包了：userId={} key={} 金额={}", userId, redId, result);
                        return result;
                    }
                }
            } catch (Exception e) {
                throw new Exception("系统异常-抢红包-加分布式锁失败");
            }
        }
        // 用户没有抢到红包
        return null;
    }

    /**
     * 点红包业务逻辑
     * @param redId 红包标识
     * @return true-缓存系统中还有红包  false-缓存系统中没有红包了
     * @throws Exception
     */
    private Boolean click(String redId) throws Exception {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String redTotalKey = redId + ":total";

        // 获取缓存中剩余红包的个数
        Object total = ops.get(redTotalKey);
        return total != null && Integer.parseInt(total.toString()) > 0;
    }
}

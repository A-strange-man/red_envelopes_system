package com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 红包生成工具类
 */
public class RedPacketUtil {
    /**
     * 发红包算法，金额参数以 '分' 为单位
     * 每人最少抢到 1分钱
     * @param totalAmount
     * @param totalPeopleNum
     * @return
     */
    public static List<Integer> divideRedPackage(Integer totalAmount, Integer totalPeopleNum) {
        List<Integer> amountList = new ArrayList<>();

        if (totalAmount > 0 && totalPeopleNum > 0) {
            // 记录剩余的总金额 和 总人数
            Integer restAmount = totalAmount;
            Integer restPeopleNum = totalPeopleNum;
            // 产生随机数的实例对象
            Random random = new Random();

            // 迭代产生随机金额，直到 totalPeopleNum == 1
            for (int i = 0; i < totalPeopleNum - 1; i++) {
                int amount = random.nextInt((restAmount / restPeopleNum) * 2 - 1) + 1;
                restAmount -= amount;
                restPeopleNum--;
                amountList.add(amount);
            }
            // 剩余最后一个金额
            amountList.add(restAmount);
        }

        return amountList;
    }
}

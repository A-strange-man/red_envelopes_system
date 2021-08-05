package com.example.dto;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 发红包请求时接收的参数对象
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketDto {

    private Integer userId;             // 用户账号ID
    @NotNull
    private Integer totalPeopleNum;     // 红包个数
    @NotNull
    private Integer totalAmount;        // 红包总金额-单位‘分’
}

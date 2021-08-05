package com.example.dto;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author CaoJing
 * @date 2021/08/05 9:52
 * 封装红包信息 - 带红包ID
 * 用于发送到延迟队列
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketInfo {

    private Integer userId;             // 用户账号ID
    @NotNull
    private String redId;               // 红包ID
    @NotNull
    private Integer totalAmount;        // 红包总金额-单位‘分’
}

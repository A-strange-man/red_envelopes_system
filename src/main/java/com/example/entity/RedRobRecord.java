package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RedRobRecord {
    @TableId(type = IdType.AUTO)
    private int id;

    private int userId;

    private String redPacket;

    private BigDecimal amount;

    private Date robTime;

    private int isActive;
}
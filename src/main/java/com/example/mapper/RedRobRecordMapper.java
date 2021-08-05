package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.RedRobRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RedRobRecordMapper extends BaseMapper<RedRobRecord> {

    Integer getSumByRedId(@Param("red_id") String redId);
}

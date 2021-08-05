package com.example.controller;

import com.example.service.IRedPacketService;
import com.example.api.BaseResponse;
import com.example.api.StatusCode;
import com.example.dto.RedPacketDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 红包处理逻辑
 */
@RestController
@RequestMapping("/red/packet")
public class RedPacketController {

    // 定义日志工具
    private static final Logger log = LoggerFactory.getLogger(RedPacketController.class);

    // 注入红包业务逻辑处理接口
    @Autowired
    private IRedPacketService redPacketService;

    /**
     * 发红包请求  -POST
     * 请求参数  -JSON格式
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = "/hand/out", method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> handOut(@Validated @RequestBody RedPacketDto dto, BindingResult result) {
        // 参数校验
        if (result.hasErrors()) {
            return new BaseResponse<String>(StatusCode.InvalidParams);
        }
        BaseResponse<String> response = new BaseResponse<String>(StatusCode.Success);

        try {
            String redId = redPacketService.handOut(dto);
            response.setDate(redId);
        } catch (Exception e) {
            log.error("发红包异常：dto={}", dto, e.fillInStackTrace());
            response = new BaseResponse<String>(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 抢红包请求 - GET
     * @param userId
     * @param redId
     * @return
     */
    @RequestMapping(value = "/hand/rob", method = RequestMethod.GET)
    public BaseResponse<BigDecimal> rob(@RequestParam Integer userId, @RequestParam String redId) {
        // 定义响应对象
        BaseResponse<BigDecimal> response = new BaseResponse(StatusCode.Success);
        try {
            BigDecimal result = redPacketService.rob(userId, redId);
            if (result != null) {
                response.setDate(result);
            } else {
                // 没有抢到红包
                response = new BaseResponse(StatusCode.Fail.getCode(), "红包被抢完了！");
            }
        } catch (Exception e) {
            log.error("抢红包发生异常：userId={} redId={} errorInfo={}", userId, redId, e.getMessage());
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}

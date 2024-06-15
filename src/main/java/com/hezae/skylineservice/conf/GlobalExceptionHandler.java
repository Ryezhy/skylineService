package com.hezae.skylineservice.conf;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.util.SaResult;
import com.hezae.skylineservice.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<SaResult> handlerSaTokenException(SaTokenException e) {

        // 根据不同异常细分状态码返回不同的提示
        if(e.getCode() == 30001) {
            log.error("状态码:"+e.getCode()+",redirect 重定向 url 是一个无效地址");
            return  new ResponseEntity<>(SaResult.error("redirect 重定向 url 是一个无效地址"), HttpStatus.UNAUTHORIZED);
        }
        if(e.getCode() == 30002) {
            log.error("状态码:"+e.getCode()+",提供的 token 是无效的");
            return  new ResponseEntity<>(SaResult.error("提供的 token 是无效的"), HttpStatus.UNAUTHORIZED);
        }
        if(e.getCode()==10002){
            log.error("状态码:"+e.getCode()+",未能获取有效的上下文");
            return  new ResponseEntity<>(SaResult.error("未能获取有效的上下文"), HttpStatus.UNAUTHORIZED);

        }
        // 更多 code 码判断 ...
        // 默认的提示
       log.error("状态码:"+e.getCode()+"服务器繁忙，请稍后重试...");
        return  new ResponseEntity<>(SaResult.error("服务器繁忙，请稍后重试..."), HttpStatus.UNAUTHORIZED);
    }
}

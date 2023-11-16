/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String username;

    @Bean
    public MessageConverter messageConverterer() {
        // 创建一个Jackson2JsonMessageConverter对象
        return new Jackson2JsonMessageConverter();
    }

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
//        log.warn("email: {}, code: {}, type: {}", email, code, type);
        SimpleMailMessage message = switch (type) {
            case "register" ->
                    createMessage("欢迎注册《世 · 界》", "您的地球通行证为：" + code + "，有效时间为3分钟，失效则无法进入地球，为了保证您的安全，切勿向他人泄露此机密信息，祝您旅途愉快。", email);
            case "reset" ->
                    createMessage("重置《世 · 界》密码", "您好，您正在重置您的密码，密码通行证为：" + code + "，有效期为3分钟，如非本人操作，请无视。", email);
            default -> null;
        };
        if (message != null) {
            javaMailSender.send(message);
//            log.warn("发送邮件成功");
        }
    }

    private SimpleMailMessage createMessage(String title, String content, String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}

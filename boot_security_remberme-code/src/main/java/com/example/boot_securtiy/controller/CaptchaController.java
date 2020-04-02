package com.example.boot_securtiy.controller;

import com.google.code.kaptcha.Producer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
@Controller
public class CaptchaController {

    @Autowired
    private Producer producer;

    @GetMapping("/captcha")
    public ResponseEntity<byte[]> getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置内容类型
        response.setContentType("image/jpeg");
        //创建验证码文本
        String captchaText = producer.createText();
        //将验证码文本设置到session
        request.getSession().setAttribute("captcha",captchaText);
        //创建验证码图片
        BufferedImage bi = producer.createImage(captchaText);
        //获取响应输出流
        ServletOutputStream out = response.getOutputStream();
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        byte[] body;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(bi,"jpg",baos);
            body = new byte[baos.size()];
            baos.write(body);
            body = baos.toByteArray();
        }
        //将图片验证码数据写道响应输出流

        return builder.body(body);
    }

}

package com.sidchai.music.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class MailSendUtil {
    @Autowired
    private JavaMailSenderImpl mailSender;

    /**
     *
     * @param multipart: 是否支持多文件上传
     * @param subject: 设置邮件的主题
     * @param text: 邮件的内容
     * @param html: 是否支持html语法
     * @param attachmentFilename: 附件名
     * @param pathname: 文件的地址
     * @param to: 收件人
     * @param from: 发件人
     * @throws MessagingException
     * @Author: sidchai
     */
    public void sendComplexMail(boolean multipart, String subject, String text, boolean html, String attachmentFilename, String pathname, String to, String from) throws MessagingException {
        //一个复杂的邮件

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //组装~
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart);

        helper.setSubject(subject);

        helper.setText(text, html);

        //附件
        helper.addAttachment(attachmentFilename, new File(pathname));
        helper.addAttachment(attachmentFilename, new File(pathname));

        //收件人
        helper.setTo(to);
        //发件人
        helper.setFrom(from);

        mailSender.send(mimeMessage);
    }

    /**
     *
     * @param subject: 邮件主题
     * @param text: 邮件内容
     * @param to: 收件人
     * @param from:发件人
     */
    public void sendSimpleMail(String subject, String text, String to, String from) {
        //一个简单的邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(text);
        //收件人
        message.setTo(to);
        //发件人
        message.setFrom(from);
        mailSender.send(message);
    }
}

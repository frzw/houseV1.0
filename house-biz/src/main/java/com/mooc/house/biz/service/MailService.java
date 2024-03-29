package com.mooc.house.biz.service;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.mooc.house.common.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String from;


  @Value("${domain.name}")
  private String domainName;


  @Autowired
  private com.mooc.house.biz.mapper.userMapper userMapper;

  //guava缓存技术，用来处理key:email的关系
  private final Cache<String, String> registerCache =
      CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15, TimeUnit.MINUTES)
          .removalListener(new RemovalListener<String, String>() {

            @Override
            public void onRemoval(RemovalNotification<String, String> notification) {//通知
              String email = notification.getValue();
              User user = new User();
              user.setEmail(email);
              List<User> targetUser = userMapper.selectUsersByQuery(user);
              if (!targetUser.isEmpty() && Objects.equal(targetUser.get(0).getEnable(), 0)) {
                userMapper.delete(email);// 代码优化: 在删除前首先判断用户是否已经被激活，对于未激活的用户进行移除操作
              }

            }
          }).build();
  
  
  private final Cache<String, String> resetCache =  CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15, TimeUnit.MINUTES).build();

  private static String getSendContentStr(String url){
    return "<!DOCTYPE html>"
            + "<html>"
            + "<head>"
            + "<title>邮件确认</title>"
            + "<meta name=\"content-type\" content=\"text/html; charset=UTF-8\">"
            + "</head>"
            + "<body>"
            + "<p style=\"color:#0FF\">这是一封确认邮件~</p>"
            + "<a href=\""+url+"\">点击确认，确认成功跳转到指定页面，"+url+"~</a>"
            + "</body>"
            + "</html>";
  }

  @Async
  public void sendMail(String title, String url, String email) {
    //SimpleMailMessage message = new SimpleMailMessage();
    //message.setFrom(from);
    //message.setSubject(title);
    //message.setTo(email);
    //message.setText(url);
    //mailSender.send(message);
    //mailSender.send(message);
    try {
      MimeMessage mimeMessage = this.mailSender.createMimeMessage();
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
      message.setFrom(from);//设置发信人，发信人需要和spring.mail.username配置的一样否则报错
      message.setTo(email);                //设置收信人
      message.setSubject(title);    //设置主题
      message.setText(getSendContentStr(url), true);    //第二个参数true表示使用HTML语言来编写邮件
      mailSender.send(mimeMessage);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 1.缓存key-email的关系 2.借助spring mail 发送邮件 3.借助异步框架进行异步操作
   * 
   * @param email
   */
  @Async
  public void registerNotify(String email) {
    //随机字符
    String randomKey = RandomStringUtils.randomAlphabetic(10);
    registerCache.put(randomKey, email);
    String url = "http://" + domainName + "/accounts/verify?key=" + randomKey;
    sendMail("房产平台激活邮件", url, email);
  }
  
  /**
   * 发送重置密码邮件
   * 
   * @param email
   */
  @Async
  public void resetNotify(String email) {
    String randomKey = RandomStringUtils.randomAlphanumeric(10);
    resetCache.put(randomKey, email);
    String content = "http://" + domainName + "/accounts/reset?key=" + randomKey;
    sendMail("房产平台密码重置邮件", content, email);
  }

  public String getResetEmail(String key){
    return resetCache.getIfPresent(key);
  }

  /**
   * 使key失效
   * @param key
   */
  public void invalidateRestKey(String key){
    resetCache.invalidate(key);
  }

  public boolean enable(String key) {
    String email = registerCache.getIfPresent(key);
    if (StringUtils.isBlank(email)) {
      return false;
    }
    User updateUser = new User();
    updateUser.setEmail(email);
    updateUser.setEnable(1);//已激活
    userMapper.update(updateUser);
    registerCache.invalidate(key);
    return true;
  }



}

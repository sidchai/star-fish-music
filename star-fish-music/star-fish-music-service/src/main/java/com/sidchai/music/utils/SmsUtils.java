package com.sidchai.music.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.result.ResultCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送SMS
 *
 * @author sidchai
 * @since 2020-05-31
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsUtils {
    private String regionId;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;

    // 短信发送
    public void send(String phone, String checkCode) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, keyId, keySecret);
        // 创建client对象
        IAcsClient client = new DefaultAcsClient(profile);

        //构建请求
        CommonRequest request = new CommonRequest();
        // 组装参数对象
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com"); //不要动
        request.setSysVersion("2017-05-25");//不要动
        request.setSysAction("SendSms");

        //自定义参数（手机号，验证码，签名，模板）
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);

        Map<String, String> param = new HashMap<>();
        param.put("code", checkCode);
        Gson gson = new Gson();
        String json = gson.toJson(param);

        request.putQueryParameter("TemplateParam", json);

        // 发送短信
        CommonResponse response = client.getCommonResponse(request);

        //response.getHttpResponse().isSuccess(); // 此处不能通过http的响应结果来判断短信是否成功发送

        // 得到json字符串格式的返回结果
        String data = response.getData();

        // 解析响应结果
        HashMap<String, String> hashMap = gson.fromJson(data, HashMap.class);
        String code = hashMap.get("Code");
        System.out.println(code);
        String message = hashMap.get("Message");
        System.out.println(message);

        if (!"OK".equals(code)) {
            log.error("短信发送失败:" + "code" + code + ",message:" + message);
            throw new MusicException(ResultCodeEnum.SMS_SEND_ERROR);
        }

    }
}

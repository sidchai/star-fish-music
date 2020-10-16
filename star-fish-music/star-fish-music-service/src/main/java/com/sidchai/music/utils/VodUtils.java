package com.sidchai.music.utils;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.result.ResultCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

/**
 *  阿里云视频点播
 * @author sidchai
 * @since 2020-06-05
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix="aliyun.vod")
public class VodUtils {

    private String keyId;
    private String keySecret;
    private String templateGroupId;
    private String workflowId;

    /**
     *  上传视频
     * @param inputStream
     * @param originalFilename
     * @return
     */
    public String uploadVideo(InputStream inputStream, String originalFilename) {
        String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        UploadStreamRequest request = new UploadStreamRequest(
                keyId,
                keySecret,
                title,
                originalFilename, inputStream);

        // 模板组id
//        request.setTemplateGroupId(templateGroupId);
        // 工作流id
//        request.setWorkflowId(workflowId);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        String videoId = response.getVideoId();
        if(StringUtils.isEmpty(videoId)) {
            log.error("阿里云上传失败:" + response.getCode() + "-" + response.getMessage());
            throw new MusicException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }


    /**
     *  client初始化
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    public static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) {
        String regionId = "cn-shanghai";  // 点播服务接入区域
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    /**
     *  删除视频文件
     * @param videoId
     * @throws ClientException
     */
    public void removeVideo(String videoId) throws ClientException {

        DefaultAcsClient client = initVodClient(keyId, keySecret);

        DeleteVideoRequest request = new DeleteVideoRequest();
        //支持传入多个视频ID，多个用逗号分隔
        request.setVideoIds(videoId);
        client.getAcsResponse(request);
    }

    /**
     *  批量删除视频
     * @param videoIdList
     * @throws ClientException
     */
    public void removeVideoByIdList(List<String> videoIdList) throws ClientException {
        DefaultAcsClient client = initVodClient(keyId, keySecret);
        DeleteVideoRequest request = new DeleteVideoRequest();

        // id列表的长度
        int size = videoIdList.size();
        // 组装好的字符串
        StringBuffer idListStr = new StringBuffer();
        for (int i = 0; i < size; i++) {
            idListStr.append(videoIdList.get(i));
            // 假设size <= 20
            if(i == size - 1 || i % 20 == 19) {
                // 删除
                //支持传入多个视频ID，多个用逗号分隔。一次性删除不能超过二十个
                log.info("idListStr = " + idListStr.toString());
                request.setVideoIds(idListStr.toString());
                client.getAcsResponse(request);
                // 重置idListStr
                idListStr = new StringBuffer();
            } else if(i < 19) {
                idListStr.append(",");
            }
        }
    }

    public String getPlayAuth(String videoSourceId) throws ClientException {
        DefaultAcsClient client = initVodClient(keyId, keySecret);

        //创建请求对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoSourceId);
        request.setAuthInfoTimeout(500L);
        //获取响应
        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }
}
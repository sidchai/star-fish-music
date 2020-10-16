package com.sidchai.music.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.result.ResultCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * 对象存储OSS
 * @author sidchai
 * @since 2020-05-25
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssUtil {
    // 外网访问节点
    private String endpoint;
    // accessKeyId和accessKeySecret是OSS的访问密钥
    private String keyId;
    private String keySecret;
    // Bucket用来管理所存储Object的存储空间
    private String bucketName;
    // 上传成功后返回的URL
    private String sufferUrl;

    public OSS getOSSClient() {
        // 创建一个OSSClient对象
        OSS ossClient = new OSSClientBuilder().build(endpoint,keyId,keySecret);
        // 判断仓库是否存在
        if(ossClient.doesBucketExist(bucketName)) {
            System.out.println("bucket创建成功");
        } else {
            System.out.println("bucket不存在,创建bucket:" + bucketName);
            // 创建存储空间。
            CreateBucketRequest bucketRequest = new CreateBucketRequest(null);
            bucketRequest.setBucketName(bucketName); // 设置仓库名称
            bucketRequest.setCannedACL(CannedAccessControlList.PublicRead);//设置权限为公共读
            ossClient.createBucket(bucketName);
        }

        return ossClient;
    }

    /**
     * 上传文件
     * @param multipartFile  需要上传的文件
     * @param module   文件夹名称
     * @return  文件在oss服务器上的url地址
     */
    public String uploadDocument(MultipartFile multipartFile, String module) {
        // 获取OSS连接
        OSS ossClient = this.getOSSClient();
        // 获取文件全称
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取文件的后缀名称
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 上传至OSS的哪个文件夹，通过filename 来指定  生成规则 （www.sidchai.com/img/20200505/aaa.jpg）
        String fileName = getFileName(module, ext);

        String url = null;

        // 通过 ossClient 来获取上传文件后返回的url
        try {
            ossClient.putObject(bucketName,fileName,new ByteArrayInputStream(multipartFile.getBytes()));

            url = sufferUrl + "/" + fileName;
            System.out.println("-------------->>上传资料成功，oss地址url:" + url);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MusicException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        } finally {
            ossClient.shutdown();
        }
        return url;
    }

    private String getFileName(String module, String ext) {
        String date = new DateTime().toString("yyyy/MM/dd");
        if(StringUtils.isEmpty(module)) { //判断类型是否无值
            module = "default";
        }
        // 为了避免图片重名，使用UUID来命名图片
        String uuid = UUID.randomUUID().toString().replace("-","");
        // 组合filename
        String fileName = "data/" + module + "/"+ date + "/" + uuid + ext;

        return fileName;
    }

    /**
     *  删除文件
     * @param url
     */
    public void removeFile(String url) {
        // 获取OSS连接
        OSS ossClient = this.getOSSClient();
        // 删除文件
        //https://sidchai.oss-cn-beijing.aliyuncs.com/data/img/userPic/admin.jpg
        String host = "https://" + bucketName + "." + endpoint + "/";
        String objectName = url.substring(host.length());
        System.out.println(objectName);
        ossClient.deleteObject(bucketName, objectName);
        ossClient.shutdown();
    }
}

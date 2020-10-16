package com.sidchai.music.controller;

import com.sidchai.music.result.R;
import com.sidchai.music.utils.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sidchai
 * @since 2020-05-30
 */
@Api(description = "阿里云文件上传管理")
@RestController
@RequestMapping("/music/oss/file")
public class FileController {

    @Autowired
    private OssUtil ossUtil;

    @ApiOperation("上传文件到阿里云oss")
    @PostMapping("/upload")
    public R uploadFile(@ApiParam(name="file", value = "文件", required = true) @RequestParam("file") MultipartFile file,
                        @ApiParam(name = "model", value = "上传目录", required = true) @RequestParam("model") String model) {
        String url = ossUtil.uploadDocument(file, model);

        return R.ok().message("文件上传成功").data("url", url);
    }
}

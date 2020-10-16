package com.sidchai.music.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @Author sidchai
 * @since 2020-05-20
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.basePackage("com.sidchai.music.controller"))
                .build();
    }

    //配置文档信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact("sidchai", "http://www.sidchai.com", "sidchai@qq.com");
        return new ApiInfo(
                "网站的API文档", // 标题
                "描述星鱼音乐网站的api接口定义", // 描述
                "v1.0", // 版本
                "http://www.sidchai.com", // 组织链接
                contact, // 联系人信息
                "Apach 2.0 许可", // 许可
                "http://www.sidchai.com", // 许可连接
                new ArrayList<>()// 扩展
        );
    }
}

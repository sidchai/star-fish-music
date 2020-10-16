package com.sidchai.music.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.RedisConstants;
import com.sidchai.music.constants.SQLConstants;
import com.sidchai.music.exception.MusicException;
import com.sidchai.music.mapper.AdminMapper;
import com.sidchai.music.pojo.Admin;
import com.sidchai.music.pojo.vo.LoginVo;
import com.sidchai.music.result.ResultCodeEnum;
import com.sidchai.music.service.AdminService;
import com.sidchai.music.utils.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author sidchai
 * @since 2020-05-19
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public Admin getAdminByName(String username) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConstants.USERNAME, username);
        wrapper.last("LIMIT 1");
        // 清除密码 防止泄露
        Admin admin = adminService.getOne(wrapper);
        admin.setPassword("");
        return admin;
    }

    // 解析数据
    @Override
    public Boolean parseContent(String keyword) throws IOException {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.like(SQLConstants.USERNAME, keyword);
        List<Admin> admins = adminService.list(wrapper);
        // 把查询到的数据存入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < admins.size(); i++) {
            bulkRequest.add(
                    new IndexRequest(BaseConstants.ADMIN_LISTS).id(admins.get(i).getId().toString())
                            .source(JSON.toJSONString(admins.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    @Override
    public List<Map<String, Object>> searchPage(String keyword, Integer currentPage, Integer pageSize) throws IOException {
        if (currentPage <= 1) {
            currentPage = 1;
        }

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(BaseConstants.ADMIN_LISTS);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(currentPage);
        sourceBuilder.size(pageSize);
        // 精准匹配
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery(SQLConstants.USERNAME, keyword + "?");
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        list.forEach(System.out::println);
        return list;
    }

    @Override
    public Admin getAdminById(Long id) {
        Admin admin = adminMapper.getAdminById(id);
        return admin;
    }

    @Override
    public Boolean updateAdmin(Admin admin) {
        return adminMapper.updateAdmin(admin);
    }

    @Override
    public String login(LoginVo loginVo, String ip) {

        String username = loginVo.getUsername();
        String password = loginVo.getPassword();

        // 校验用户信息是否存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        boolean isEmail = CheckUtils.checkEmail(username);
        boolean isPhone = CheckUtils.checkPhoneNumber(username);
        if(isEmail) {
            queryWrapper.eq(SQLConstants.EMAIL, username);
        } else if(isPhone) {
            queryWrapper.eq(SQLConstants.PHONE, username);
        } else {
            queryWrapper.eq(SQLConstants.USERNAME, username);
        }
        Admin admin = adminService.getOne(queryWrapper);
        if (admin == null) {
            setLoginCommit(ip);
            throw new MusicException(ResultCodeEnum.LOGIN_USERNAME_ERROR);
        }

        // 校验密码是否正确
        SimpleHash hash = new SimpleHash(BaseConstants.ALGORITHM_NAME, password, ByteSource.Util.bytes(admin.getUsername()), BaseConstants.HASH_ITERATIONS);
        if (!hash.toString().equals(admin.getPassword())) {
            setLoginCommit(ip);
            throw new MusicException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 校验用户是否被禁用
        if (admin.getStatus() == 0) { // 封禁
            throw new MusicException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 把当前登录信息存入数据库中
        admin.setLastLoginIp(ip);
        admin.setLastLoginTime(new Date());
        Integer count = admin.getLoginCount() + 1;
        admin.setLoginCount(count);
        adminService.updateById(admin);

        // 登录 : 生成token
        // 创建jwt信息对象
        JwtInfo info = new JwtInfo();
        // 设置信息
        info.setUsername(admin.getUsername());
        info.setAvatar(admin.getAvatar());
        info.setId(admin.getId().toString());

        String jwtToken = JwtUtils.getJwtToken(info, 3600);
        return jwtToken;
    }

    @Override
    public boolean removerAvatar(Long id) {

        Admin admin = adminService.getById(id);
        if (admin != null) {
            String url = admin.getAvatar();
            if(!StringUtils.isEmpty(url)) {
                ossUtil.removeFile(url);
                return true;
            }
        }

        return false;
    }

    /**
     * 设置登录限制，返回剩余次数
     * 密码错误五次，将会锁定10分钟
     *
     * @param ip
     */
    private Integer setLoginCommit(String ip) {
        String count = (String) redisUtil.get(RedisConstants.LOGIN_LIMIT + RedisConstants.SEGMENTATION + ip);
        Integer surplusCount = 5;
        if (!StringUtils.isEmpty(count)) {
            Integer countTemp = Integer.valueOf(count) + 1;
            surplusCount = surplusCount - countTemp;
            redisUtil.setEx(RedisConstants.LOGIN_LIMIT + RedisConstants.SEGMENTATION + ip, String.valueOf(countTemp), 10, TimeUnit.MINUTES);
        } else {
            surplusCount = surplusCount - 1;
            redisUtil.setEx(RedisConstants.LOGIN_LIMIT + RedisConstants.SEGMENTATION + ip, "1", 30, TimeUnit.MINUTES);
        }

        return surplusCount;
    }
}

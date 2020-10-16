package com.sidchai.music.utils;

import com.sidchai.music.constants.BaseConstants;
import com.sidchai.music.constants.SQLConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author helen
 * @since 2019/10/16
 */
public class JwtUtils {

    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; // 秘钥

    private static Key getKeyInstance(){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(APP_SECRET);
        return new SecretKeySpec(bytes,signatureAlgorithm.getJcaName());
    }

    public static String getJwtToken(JwtInfo jwtInfo, int expire){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("sidchai")//主题
                .setIssuedAt(new Date())//颁发时间
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())//过期时间
                .claim(SQLConstants.ID, jwtInfo.getId())//用户id
                .claim(SQLConstants.USERNAME, jwtInfo.getUsername())//用户昵称
                .claim(SQLConstants.AVATAR, jwtInfo.getAvatar())//用户头像
                .signWith(SignatureAlgorithm.HS256, getKeyInstance())//签名
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkJwtTToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkJwtTToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader(BaseConstants.TOKEN);
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取管理员id
     * @param request
     * @return
     */
    public static JwtInfo getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(BaseConstants.TOKEN);
        if(StringUtils.isEmpty(jwtToken)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        String avatar = (String) claims.get("avatar");
        JwtInfo jwtInfo = null;
        if(StringUtils.isEmpty(avatar)) {
            avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
            jwtInfo = new JwtInfo(claims.get(SQLConstants.ID).toString(), claims.get(SQLConstants.USERNAME).toString(), avatar);
        } else {
            jwtInfo = new JwtInfo(claims.get(SQLConstants.ID).toString(), claims.get(SQLConstants.USERNAME).toString(), claims.get(SQLConstants.AVATAR).toString());
        }
        return jwtInfo;
    }
}

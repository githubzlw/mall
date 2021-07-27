package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.portal.domain.FacebookPojo;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.UmsMemberCacheService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.security.util.JwtTokenUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

//    private final static String FACEBOOK_LOGIN_URL = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s/sso/facebookLogin?&scope=email,public_profile&fields=name,email";
//    private final static String FACEBOOK_ME_URL = "https://graph.facebook.com/oauth/access_token?redirect_uri=%s/sso/facebookLogin?&client_id=%s&client_secret=%s&code=%s";
//    private final static String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/me?fields=id,name,email&access_token=%s";
//    private final static String SITE_URL = "https://app.busysell.com";

    private final static String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/%s?fields=email&access_token=%s";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private UmsMemberCacheService memberCacheService;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;
    @Value("${tpurl.googleid}")
    public String GOOGLE_CLIENT_ID;
//    @Value("${tpurl.facebookClientId}")
//    public String FACE_BOOK_CLIENTID;
//    @Value("${tpurl.facebookClientSecret}")
//    public String FACE_BOOK_CLIENT_SECRET;



    @Override
    public UmsMember getByUsername(String username) {
        UmsMember member = memberCacheService.getMember(username);
        if(member!=null) return member;
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            member = memberList.get(0);
            memberCacheService.setMember(member);
            return member;
        }
        return null;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String username, String password, String organizationname,String monthlyOrders,Integer loginType, Integer countryId) {
//        //验证验证码
//        if(!verifyAuthCode(authCode,telephone)){
//            Asserts.fail("验证码错误");
//        }
        String telephone = username;
        //如果用户用改email从我们网站上登录，提示该邮箱已使用,查询是否已有该用户
        UmsMemberExample exampleSf = new UmsMemberExample();
        exampleSf.createCriteria().andUsernameEqualTo(username);
        exampleSf.createCriteria().andLoginTypeEqualTo(loginType);
        List<UmsMember> umsMembersSf = memberMapper.selectByExample(exampleSf);
        if (!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() != 0) {
            Asserts.fail("The email is used, please login with "+username);
        }else if(!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() == 0){
            Asserts.fail("The email is used");
        }

        //没有该用户进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }
        umsMember.setOrganizationname(organizationname);
        umsMember.setMonthlyOrders(monthlyOrders);
        umsMember.setLoginType(loginType);
        umsMember.setCountryId(countryId);
        memberMapper.insert(umsMember);
        umsMember.setPassword(null);
    }

    @Override
    public String generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        memberCacheService.setAuthCode(telephone,sb.toString());
        return sb.toString();
    }

    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(memberList)){
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(passwordEncoder.encode(password));
        memberMapper.updateByPrimaryKeySelective(umsMember);
        memberCacheService.delMember(umsMember.getId());
    }

    @Override
    public void resetPassword(Long memberId, String password) {

        UmsMember record = new UmsMember();
        record.setId(memberId);
        record.setPassword(passwordEncoder.encode(password));
        memberMapper.updateByPrimaryKeySelective(record);
        memberCacheService.delMember(memberId);
    }

    @Override
    public int updateUserInfo(String niceName, String monthlyOrderQuantity, String organizationName) {
        UmsMember currentMember = this.getCurrentMember();
        UmsMember umsMember = new UmsMember();
        umsMember.setId(currentMember.getId());
        //umsMember.setUsername(currentMember.getUsername());
        umsMember.setNickname(niceName);
        umsMember.setMonthlyOrders(monthlyOrderQuantity);
        if (StrUtil.isNotEmpty(niceName)) {
            currentMember.setNickname(niceName);
        }
        umsMember.setOrganizationname(organizationName);
        return memberMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public UmsMember getCurrentMember() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        return memberDetails.getUmsMember();
    }

    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember record=new UmsMember();
        record.setId(id);
        record.setIntegration(integration);
        memberMapper.updateByPrimaryKeySelective(record);
        memberCacheService.delMember(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UmsMember member = getByUsername(username);
        if(member!=null){
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshHeadToken(token);
    }


    /**
     * call googleAuth
     * @return
     * @throws IOException
     */
    @Override
    public ImmutablePair<String, String> googleAuth(String idTokenString) throws IOException {

        try {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

            LOGGER.info("idToken", idTokenString);
            LOGGER.info("GOOGLE_CLIENT_ID", GOOGLE_CLIENT_ID);

            GoogleIdToken idToken = verifier.verify(idTokenString);
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleUserId = payload.getSubject();
            String googleEmail = payload.getEmail();

            return new ImmutablePair<>(googleUserId, googleEmail);

        } catch (GeneralSecurityException | IOException e) {
            throw new IOException("googleAuth.GeneralSecurityException");
        }
    }

//    /**
//     * get facebook login url
//     * @return
//     */
//    @Override
//    public String getFacebookUrl() {
//
//        return String.format(FACEBOOK_LOGIN_URL
//                , FACE_BOOK_CLIENTID, SITE_URL);
//    }


    /**
     * call facebookAuth
     * @return
     * @throws IOException
     */
    public FacebookPojo facebookAuth(String facebookId,String fToken) {

        if(StringUtils.isEmpty(facebookId)){
            throw new IllegalArgumentException("facebookId is empty");
        }

//        String accessTokenUrl = String.format(FACEBOOK_ME_URL
//                ,SITE_URL, FACE_BOOK_CLIENTID, FACE_BOOK_CLIENT_SECRET, code);
//        LOGGER.info("accessTokenURL:[{}]", accessTokenUrl);
//        LOGGER.info("faceCode", code);
//        RestTemplate restTemplate = new RestTemplate();
//        HashMap<String, String> result = restTemplate.getForObject(accessTokenUrl, HashMap.class);
//        assert result != null;
//        String accessToken = result.get("access_token");
//        LOGGER.info("get access token:[{}] success", accessToken);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(String.format(FACEBOOK_TOKEN_URL,facebookId, fToken), FacebookPojo.class);

    }
    @Override
    public int updateShopifyInfo(Long id, String shopifyName, Integer shopifyFlag) {
        UmsMember tempMember = new UmsMember();
        tempMember.setId(id);
        tempMember.setShopifyName(shopifyName);
        tempMember.setShopifyFlag(shopifyFlag);
        return this.memberMapper.updateByPrimaryKeySelective(tempMember);
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone){
        if(StringUtils.isEmpty(authCode)){
            return false;
        }
        String realAuthCode = memberCacheService.getAuthCode(telephone);
        return authCode.equals(realAuthCode);
    }

    @Override
    public int updateGuidedFlag(Long id) {
        UmsMember tempMember = new UmsMember();
        tempMember.setId(id);
        tempMember.setGuidedFlag(1);
        return this.memberMapper.updateByPrimaryKeySelective(tempMember);
    }

    @Override
    public String verifyOldPassword(String username, String password) {

        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return "fail";
            }
            return "success";
        } catch (Exception e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
            return "fail";
        }

    }

}

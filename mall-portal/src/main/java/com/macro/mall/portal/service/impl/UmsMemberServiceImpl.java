package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
 * ????????????Service?????????
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
    public UmsMember getByUsernameNoCache(String username) {
        UmsMember member = null;
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            member = memberList.get(0);
            return member;
        }
        return member;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String username, String password, String organizationname,String monthlyOrders,Integer loginType, Integer countryId) {
//        //???????????????
//        if(!verifyAuthCode(authCode,telephone)){
//            Asserts.fail("???????????????");
//        }
        String telephone = username;
        //??????????????????email???????????????????????????????????????????????????,???????????????????????????
        UmsMemberExample exampleSf = new UmsMemberExample();
        exampleSf.createCriteria().andUsernameEqualTo(username);
        exampleSf.createCriteria().andLoginTypeEqualTo(loginType);
        List<UmsMember> umsMembersSf = memberMapper.selectByExample(exampleSf);
        if (!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() != 0) {
            Asserts.fail("The email is used, please login with "+username);
        }else if(!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() == 0){
            Asserts.fail("The email is used");
        }

        //?????????????????????????????????
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //?????????????????????????????????
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
    public void registerNew(String username, String password, String organizationname,String monthlyOrders,Integer loginType, Integer countryId) {
//        //???????????????
//        if(!verifyAuthCode(authCode,telephone)){
//            Asserts.fail("???????????????");
//        }
        String telephone = username;
        //??????????????????email???????????????????????????????????????????????????,???????????????????????????
        UmsMemberExample exampleSf = new UmsMemberExample();
        exampleSf.createCriteria().andUsernameEqualTo(username);
        exampleSf.createCriteria().andLoginTypeEqualTo(loginType);
        List<UmsMember> umsMembersSf = memberMapper.selectByExample(exampleSf);
        if (!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() != 0) {
            Asserts.fail("The email is used, please login with "+username);
        }else if(!CollectionUtils.isEmpty(umsMembersSf) && umsMembersSf.get(0).getLoginType() == 0){
            Asserts.fail("The email is used");
        }

        //?????????????????????????????????
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //?????????????????????????????????
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
            Asserts.fail("??????????????????");
        }
        //???????????????
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("???????????????");
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
        throw new UsernameNotFoundException("????????????????????????");
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //????????????????????????????????????
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("???????????????");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("????????????:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String loginNoPassWord(String username) {
        String token = null;
        //????????????????????????????????????
        try {
            UserDetails userDetails = loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("????????????:{}", e.getMessage());
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

    //?????????????????????????????????
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

        //????????????????????????????????????
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return "fail";
            }
            return "success";
        } catch (Exception e) {
            LOGGER.warn("????????????:{}", e.getMessage());
            return "fail";
        }

    }


    @Override
    public void updateSecurityContext() {
        UmsMember umsMember = this.memberMapper.selectByPrimaryKey(getCurrentMember().getId());
        MemberDetails userDetails = new MemberDetails(umsMember);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.memberCacheService.delMember(umsMember.getId());
            this.memberCacheService.setMember(umsMember);
    }


    @Override
    public void setOtherInfo(UmsMember umsUp) {
        UmsMember temp = new UmsMember();
        temp.setId(umsUp.getId());
        if(null == umsUp.getSourcingTypeOfShipping() || umsUp.getSourcingTypeOfShipping() == 0){
            temp.setSourcingTypeOfShipping(0);
        } else{
            temp.setSourcingTypeOfShipping(umsUp.getSourcingTypeOfShipping());
        }

        if(null == umsUp.getSourcingChooseType() || umsUp.getSourcingChooseType() == 0){
            temp.setSourcingChooseType(0);
        } else{
            temp.setSourcingChooseType(umsUp.getSourcingChooseType());
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingCountryName())){
            temp.setSourcingCountryName(umsUp.getSourcingCountryName());
        } else{
            temp.setSourcingCountryName("");
        }

        if(null == umsUp.getSourcingCountryId() || umsUp.getSourcingCountryId() == 0){
            temp.setSourcingChooseType(0);
        } else{
            temp.setSourcingCountryId(umsUp.getSourcingCountryId());
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingStateName())){
            temp.setSourcingStateName(umsUp.getSourcingStateName());
        } else{
            temp.setSourcingStateName("");
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingCustomType())){
            temp.setSourcingCustomType(umsUp.getSourcingCustomType());
        } else{
            temp.setSourcingCustomType("");
        }

        if(null == umsUp.getSourcingOrderQuantity() || umsUp.getSourcingOrderQuantity() == 0){
            temp.setSourcingOrderQuantity(0);
        } else{
            temp.setSourcingOrderQuantity(umsUp.getSourcingOrderQuantity());
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingRemark())){
            temp.setSourcingRemark(umsUp.getSourcingRemark());
        } else{
            temp.setSourcingRemark("");
        }

        if(null == umsUp.getSourcingPrcFlag() || umsUp.getSourcingPrcFlag() == 0){
            temp.setSourcingPrcFlag(0);
        } else{
            temp.setSourcingPrcFlag(umsUp.getSourcingPrcFlag());
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingCifPort())){
            temp.setSourcingCifPort(umsUp.getSourcingCifPort());
        } else{
            temp.setSourcingCifPort("");
        }

        if(StrUtil.isNotBlank(umsUp.getSourcingFbaWarehouse())){
            temp.setSourcingFbaWarehouse(umsUp.getSourcingFbaWarehouse());
        } else{
            temp.setSourcingFbaWarehouse("");
        }

        this.memberMapper.updateByPrimaryKeySelective(temp);

        UmsMember usm = this.memberMapper.selectByPrimaryKey(getCurrentMember().getId());
        MemberDetails userDetails = new MemberDetails(usm);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.memberCacheService.delMember(usm.getId());
            this.memberCacheService.setMember(usm);
    }


    @Override
    public void setLogo(UmsMember umsUp) {
        this.memberMapper.updateByPrimaryKeySelective(umsUp);

        UmsMember usm = this.memberMapper.selectByPrimaryKey(getCurrentMember().getId());
        MemberDetails userDetails = new MemberDetails(usm);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.memberCacheService.delMember(usm.getId());
            this.memberCacheService.setMember(usm);
    }


    @Override
    public int clearOtherShopifyInfo(Long id,String shopifyName) {
        if (null != id && id > 0 && StrUtil.isNotBlank(shopifyName)) {
            return this.memberMapper.clearOtherShopifyInfo(id, shopifyName);
        }
        return 0;
    }

    @Override
    public int getByShopifyName(String shopifyName) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andShopifyNameEqualTo(shopifyName);
        List<UmsMember> umsMembers = this.memberMapper.selectByExample(example);
        int total = 0;
        if(CollectionUtil.isNotEmpty(umsMembers)){
            total = umsMembers.size();
            umsMembers.clear();
        }
        return total;
    }

}

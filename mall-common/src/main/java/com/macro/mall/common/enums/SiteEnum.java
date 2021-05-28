package com.macro.mall.common.enums;

import com.alibaba.fastjson.JSONObject;

/**
 * @author luohao
 * @date 2019/6/25
 */
public enum SiteEnum {

    IMPORTX(1,"https://www.import-express.com","IMPORTX",'I'),
    KIDS(2,"https://www.kidscharming.com","KIDS",'K'),
    PETS(4,"https://www.petstoreinc.com","PETS",'P'),
    HOME(8,"https://www.homeproductimport.com","HOME",'H'),
    MEDIC(16,"https://www.medicaldevicefactory.com","MEDIC",'M'),
    SOURCING(11,"https://www.importx.com","SOURCING",'S'),
    ALLHOST(32,"");
    private int code;

    private String url;

    private String name;

    private char siteType;

    SiteEnum(int code){
        this.code = code;
    }
    SiteEnum(int code, String url){
        this.url = url;
        this.code = code;
    }
    SiteEnum(int code, String url , String name, char siteType){
        this.url = url;
        this.code = code;
        this.name = name;
        this.siteType = siteType;
    }
    public int getCode(){
        return this.code;
    }

    public String getUrl() {
        return url;
    }

    public char getSiteType() {
        return siteType;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("code", this.code);
        json.put("url", this.url);
        json.put("siteType", this.siteType);
        return json.toString();
    }

    public static void main(String[] args){
        System.out.println(SiteEnum.valueOf("importx"));
        System.out.println(SiteEnum.valueOf("pets"));
        System.out.println(SiteEnum.valueOf("pets").code);
    }
}

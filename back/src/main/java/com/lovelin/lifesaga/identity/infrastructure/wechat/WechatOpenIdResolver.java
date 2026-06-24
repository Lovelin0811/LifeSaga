package com.lovelin.lifesaga.identity.infrastructure.wechat;

public interface WechatOpenIdResolver {

    String resolveOpenId(String code);
}

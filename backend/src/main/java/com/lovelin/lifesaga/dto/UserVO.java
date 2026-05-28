package com.lovelin.lifesaga.dto;

import com.lovelin.lifesaga.model.User;

/**
 * 返回给前端的安全用户视图，排除 openid 等敏感字段
 */
public class UserVO {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private int level;
    private int xp;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.id = user.getId();
        vo.nickname = user.getNickname();
        vo.avatarUrl = user.getAvatarUrl();
        vo.level = user.getLevel();
        vo.xp = user.getXp();
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
}

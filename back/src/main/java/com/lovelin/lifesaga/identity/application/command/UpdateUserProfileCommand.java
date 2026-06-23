package com.lovelin.lifesaga.identity.application.command;

import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;

public record UpdateUserProfileCommand(
        UserId userId,
        UserNickname userNickname,
        UserAvatarUrl userAvatarUrl
) {

    public UpdateUserProfileCommand {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (userNickname == null && userAvatarUrl == null) {
            throw new IllegalArgumentException("用户资料更新内容不能为空");
        }
    }
}

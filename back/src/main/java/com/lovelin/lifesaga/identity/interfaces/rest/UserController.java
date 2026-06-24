package com.lovelin.lifesaga.identity.interfaces.rest;

import com.lovelin.lifesaga.identity.application.command.UpdateUserProfileCommand;
import com.lovelin.lifesaga.gallery.application.query.GalleryItemView;
import com.lovelin.lifesaga.gallery.application.service.GalleryQueryApplicationService;
import com.lovelin.lifesaga.identity.application.service.UpdateUserProfileApplicationService;
import com.lovelin.lifesaga.identity.application.service.UserQueryApplicationService;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryApplicationService userQueryApplicationService;
    private final UpdateUserProfileApplicationService updateUserProfileApplicationService;
    private final GalleryQueryApplicationService galleryQueryApplicationService;

    public UserController(
            UserQueryApplicationService userQueryApplicationService,
            UpdateUserProfileApplicationService updateUserProfileApplicationService,
            GalleryQueryApplicationService galleryQueryApplicationService
    ) {
        this.userQueryApplicationService = userQueryApplicationService;
        this.updateUserProfileApplicationService = updateUserProfileApplicationService;
        this.galleryQueryApplicationService = galleryQueryApplicationService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpServletRequest httpServletRequest) {
        User user = userQueryApplicationService.getUserById(new UserId(currentUserId(httpServletRequest)));
        return ApiResponse.success(UserResponse.from(user));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> update(
            @RequestBody UpdateUserRequest updateUserRequest,
            HttpServletRequest httpServletRequest
    ) {
        User user = updateUserProfileApplicationService.updateUserProfile(new UpdateUserProfileCommand(
                new UserId(currentUserId(httpServletRequest)),
                toUserNickname(updateUserRequest.nickname()),
                toUserAvatarUrl(updateUserRequest.avatarUrl())
        ));
        return ApiResponse.success(UserResponse.from(user));
    }

    @GetMapping("/me/albums")
    public ApiResponse<java.util.List<AlbumItemResponse>> albums(HttpServletRequest httpServletRequest) {
        java.util.List<AlbumItemResponse> albums = galleryQueryApplicationService
                .listByUserId(new UserId(currentUserId(httpServletRequest)))
                .stream()
                .map(AlbumItemResponse::from)
                .toList();
        return ApiResponse.success(albums);
    }

    private long currentUserId(HttpServletRequest httpServletRequest) {
        Object userId = httpServletRequest.getAttribute("userId");
        if (!(userId instanceof Long value)) {
            throw new IllegalStateException("未登录");
        }
        return value;
    }

    private UserNickname toUserNickname(String nickname) {
        return nickname == null ? null : new UserNickname(nickname);
    }

    private UserAvatarUrl toUserAvatarUrl(String avatarUrl) {
        return avatarUrl == null ? null : new UserAvatarUrl(avatarUrl);
    }

    public record UpdateUserRequest(String nickname, String avatarUrl) {
    }

    public record UserResponse(
            Long id,
            String nickname,
            String avatarUrl,
            int level,
            int xp
    ) {

        static UserResponse from(User user) {
            return new UserResponse(
                    user.userId() == null ? null : user.userId().value(),
                    user.userNickname().value(),
                    user.userAvatarUrl().value(),
                    user.userLevel().value(),
                    user.userExperience().value()
            );
        }
    }

    public record AlbumItemResponse(
            String url,
            String title,
            String sagaName,
            java.time.LocalDateTime nodeTime
    ) {

        static AlbumItemResponse from(GalleryItemView galleryItemView) {
            return new AlbumItemResponse(
                    galleryItemView.url(),
                    galleryItemView.title(),
                    galleryItemView.sagaName(),
                    galleryItemView.photoTime()
            );
        }
    }

    public record ApiResponse<T>(int code, T data, String message) {

        static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(200, data, "success");
        }
    }
}

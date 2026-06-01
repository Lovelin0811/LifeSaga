package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.dto.UserVO;
import com.lovelin.lifesaga.dto.AlbumItemVO;
import com.lovelin.lifesaga.model.User;
import com.lovelin.lifesaga.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        Long userId = getUserId(request);
        User user = userService.getById(userId);
        return Map.of("code", 200, "data", UserVO.from(user), "message", "success");
    }

    @PutMapping("/me")
    public Map<String, Object> update(@RequestBody User user, HttpServletRequest request) {
        Long userId = getUserId(request);
        User updated = userService.update(userId, user);
        return Map.of("code", 200, "data", UserVO.from(updated), "message", "success");
    }

    @GetMapping("/me/albums")
    public Map<String, Object> albums(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<AlbumItemVO> albums = userService.listAlbums(userId);
        return Map.of("code", 200, "data", albums, "message", "success");
    }
}

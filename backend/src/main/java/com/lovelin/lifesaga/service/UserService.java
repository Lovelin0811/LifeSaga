package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.User;
import com.lovelin.lifesaga.dto.AlbumItemVO;
import com.lovelin.lifesaga.repository.AlbumRepository;
import com.lovelin.lifesaga.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    public UserService(UserRepository userRepository, AlbumRepository albumRepository) {
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("用户不存在"));
    }

    public User update(Long userId, User user) {
        User existing = getById(userId);
        if (user.getNickname() != null) existing.setNickname(user.getNickname());
        if (user.getAvatarUrl() != null) existing.setAvatarUrl(user.getAvatarUrl());
        userRepository.update(existing);
        return getById(userId);
    }

    public List<AlbumItemVO> listAlbums(Long userId) {
        return albumRepository.findByUserId(userId);
    }
}

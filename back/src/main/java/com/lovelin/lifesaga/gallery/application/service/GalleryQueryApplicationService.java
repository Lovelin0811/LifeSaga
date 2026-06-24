package com.lovelin.lifesaga.gallery.application.service;

import com.lovelin.lifesaga.gallery.application.query.GalleryItemView;
import com.lovelin.lifesaga.gallery.application.query.GalleryQueryRepository;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GalleryQueryApplicationService {

    private final GalleryQueryRepository galleryQueryRepository;

    public GalleryQueryApplicationService(GalleryQueryRepository galleryQueryRepository) {
        this.galleryQueryRepository = galleryQueryRepository;
    }

    @Transactional(readOnly = true)
    public List<GalleryItemView> listByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return galleryQueryRepository.findByUserId(userId);
    }
}

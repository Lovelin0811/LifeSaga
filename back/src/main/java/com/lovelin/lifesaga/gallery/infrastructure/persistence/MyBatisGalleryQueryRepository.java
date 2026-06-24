package com.lovelin.lifesaga.gallery.infrastructure.persistence;

import com.lovelin.lifesaga.gallery.application.query.GalleryItemView;
import com.lovelin.lifesaga.gallery.application.query.GalleryQueryRepository;
import com.lovelin.lifesaga.gallery.infrastructure.persistence.mapper.GalleryMapper;
import com.lovelin.lifesaga.gallery.infrastructure.persistence.record.GalleryItemRecord;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisGalleryQueryRepository implements GalleryQueryRepository {

    private final GalleryMapper galleryMapper;

    public MyBatisGalleryQueryRepository(GalleryMapper galleryMapper) {
        this.galleryMapper = galleryMapper;
    }

    @Override
    public List<GalleryItemView> findByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return galleryMapper.findByUserId(userId.value()).stream()
                .map(this::toView)
                .toList();
    }

    private GalleryItemView toView(GalleryItemRecord galleryItemRecord) {
        return new GalleryItemView(
                galleryItemRecord.getUrl(),
                galleryItemRecord.getTitle(),
                galleryItemRecord.getSagaName(),
                galleryItemRecord.getPhotoTime()
        );
    }
}

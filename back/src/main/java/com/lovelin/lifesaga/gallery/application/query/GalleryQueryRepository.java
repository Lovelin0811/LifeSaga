package com.lovelin.lifesaga.gallery.application.query;

import com.lovelin.lifesaga.identity.domain.model.UserId;

import java.util.List;

public interface GalleryQueryRepository {

    List<GalleryItemView> findByUserId(UserId userId);
}

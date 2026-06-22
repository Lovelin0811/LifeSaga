package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodePhotosTest {

    @Test
    void shouldCreatePhotos() {
        SagaNodePhotos sagaNodePhotos = new SagaNodePhotos(List.of("https://example.com/photo.jpg"));

        assertEquals(List.of("https://example.com/photo.jpg"), sagaNodePhotos.values());
    }

    @Test
    void shouldRejectEmptyPhotos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodePhotos(List.of())
        );
    }

    @Test
    void shouldRejectMoreThanNinePhotos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodePhotos(List.of(
                        "1", "2", "3", "4", "5",
                        "6", "7", "8", "9", "10"
                ))
        );
    }
}

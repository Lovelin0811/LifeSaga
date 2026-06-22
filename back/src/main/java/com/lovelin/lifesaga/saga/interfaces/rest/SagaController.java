package com.lovelin.lifesaga.saga.interfaces.rest;

import com.lovelin.lifesaga.saga.application.command.CreateSagaCommand;
import com.lovelin.lifesaga.saga.application.command.CompleteSagaCommand;
import com.lovelin.lifesaga.saga.application.command.DeleteSagaCommand;
import com.lovelin.lifesaga.saga.application.command.UpdateSagaCommand;
import com.lovelin.lifesaga.saga.application.service.CompleteSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.CreateSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.DeleteSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.SagaQueryApplicationService;
import com.lovelin.lifesaga.saga.application.service.UpdateSagaApplicationService;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final CreateSagaApplicationService createSagaApplicationService;
    private final UpdateSagaApplicationService updateSagaApplicationService;
    private final CompleteSagaApplicationService completeSagaApplicationService;
    private final DeleteSagaApplicationService deleteSagaApplicationService;
    private final SagaQueryApplicationService sagaQueryApplicationService;

    public SagaController(
            CreateSagaApplicationService createSagaApplicationService,
            UpdateSagaApplicationService updateSagaApplicationService,
            CompleteSagaApplicationService completeSagaApplicationService,
            DeleteSagaApplicationService deleteSagaApplicationService,
            SagaQueryApplicationService sagaQueryApplicationService
    ) {
        this.createSagaApplicationService = createSagaApplicationService;
        this.updateSagaApplicationService = updateSagaApplicationService;
        this.completeSagaApplicationService = completeSagaApplicationService;
        this.deleteSagaApplicationService = deleteSagaApplicationService;
        this.sagaQueryApplicationService = sagaQueryApplicationService;
    }

    @GetMapping
    public ApiResponse<List<SagaResponse>> list(HttpServletRequest httpServletRequest) {
        Long userId = currentUserId(httpServletRequest);
        List<SagaResponse> sagas = sagaQueryApplicationService.listOwnerSagas(new SagaOwnerId(userId)).stream()
                .map(SagaResponse::from)
                .toList();
        return ApiResponse.success(sagas);
    }

    @GetMapping("/public")
    public ApiResponse<List<SagaResponse>> listPublic() {
        List<SagaResponse> sagas = sagaQueryApplicationService.listPublicSagas().stream()
                .map(SagaResponse::from)
                .toList();
        return ApiResponse.success(sagas);
    }

    @GetMapping("/{id}")
    public ApiResponse<SagaDetailResponse> detail(
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = currentUserId(httpServletRequest);
        SagaQueryApplicationService.SagaDetail sagaDetail = sagaQueryApplicationService.getSagaDetail(
                new SagaId(id),
                new SagaOwnerId(userId)
        );
        return ApiResponse.success(SagaDetailResponse.from(sagaDetail));
    }

    @PostMapping
    public ApiResponse<SagaResponse> create(
            @RequestBody CreateSagaRequest createSagaRequest,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = currentUserId(httpServletRequest);
        Saga saga = createSagaApplicationService.createSaga(new CreateSagaCommand(
                new SagaOwnerId(userId),
                new SagaName(createSagaRequest.name()),
                SagaType.valueOf(createSagaRequest.type().toUpperCase(Locale.ROOT)),
                createSagaRequest.coverUrl(),
                createSagaRequest.description(),
                Boolean.TRUE.equals(createSagaRequest.publicVisible())
        ));
        return ApiResponse.success(SagaResponse.from(saga));
    }

    @PutMapping("/{id}")
    public ApiResponse<SagaResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateSagaRequest updateSagaRequest,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = currentUserId(httpServletRequest);
        Saga saga = updateSagaApplicationService.updateSaga(new UpdateSagaCommand(
                new SagaId(id),
                new SagaOwnerId(userId),
                new SagaName(updateSagaRequest.name()),
                SagaType.valueOf(updateSagaRequest.type().toUpperCase(Locale.ROOT)),
                updateSagaRequest.coverUrl(),
                updateSagaRequest.description(),
                Boolean.TRUE.equals(updateSagaRequest.publicVisible())
        ));
        return ApiResponse.success(SagaResponse.from(saga));
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<SagaResponse> complete(
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = currentUserId(httpServletRequest);
        Saga saga = completeSagaApplicationService.completeSaga(new CompleteSagaCommand(
                new SagaId(id),
                new SagaOwnerId(userId)
        ));
        return ApiResponse.success(SagaResponse.from(saga));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = currentUserId(httpServletRequest);
        deleteSagaApplicationService.deleteSaga(new DeleteSagaCommand(
                new SagaId(id),
                new SagaOwnerId(userId)
        ));
        return ApiResponse.success(null);
    }

    private Long currentUserId(HttpServletRequest httpServletRequest) {
        Object userId = httpServletRequest.getAttribute("userId");
        if (!(userId instanceof Long value)) {
            throw new IllegalStateException("未登录");
        }
        return value;
    }

    public record CreateSagaRequest(
            String name,
            String type,
            String coverUrl,
            String description,
            Boolean publicVisible
    ) {
    }

    public record UpdateSagaRequest(
            String name,
            String type,
            String coverUrl,
            String description,
            Boolean publicVisible
    ) {
    }

    public record SagaResponse(
            Long id,
            Long ownerId,
            String name,
            String type,
            String coverUrl,
            String description,
            String status,
            boolean publicVisible,
            int nodeCount,
            String rarity,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {

        static SagaResponse from(Saga saga) {
            return new SagaResponse(
                    saga.sagaId() == null ? null : saga.sagaId().value(),
                    saga.sagaOwnerId().value(),
                    saga.sagaName().value(),
                    saga.sagaType().name(),
                    saga.coverUrl(),
                    saga.description(),
                    saga.sagaStatus().name(),
                    saga.publicVisible(),
                    saga.nodeCount(),
                    saga.sagaRarity().name(),
                    saga.startedAt(),
                    saga.endedAt()
            );
        }
    }

    public record SagaDetailResponse(
            SagaResponse saga,
            List<SagaNodeResponse> nodes
    ) {

        static SagaDetailResponse from(SagaQueryApplicationService.SagaDetail sagaDetail) {
            return new SagaDetailResponse(
                    SagaResponse.from(sagaDetail.saga()),
                    sagaDetail.sagaNodes().stream()
                            .map(SagaNodeResponse::from)
                            .toList()
            );
        }
    }

    public record SagaNodeResponse(
            Long id,
            Long sagaId,
            String title,
            int sortOrder,
            String description,
            String location,
            List<String> photos,
            LocalDateTime nodeTime,
            boolean milestone,
            boolean favorited
    ) {

        static SagaNodeResponse from(SagaQueryApplicationService.SagaNodeDetail sagaNodeDetail) {
            SagaNode sagaNode = sagaNodeDetail.sagaNode();
            return new SagaNodeResponse(
                    sagaNode.sagaNodeId() == null ? null : sagaNode.sagaNodeId().value(),
                    sagaNode.sagaId().value(),
                    sagaNode.sagaNodeTitle().value(),
                    sagaNode.sagaNodeOrder().value(),
                    sagaNode.sagaNodeDescription() == null ? null : sagaNode.sagaNodeDescription().value(),
                    sagaNode.sagaNodeLocation() == null ? null : sagaNode.sagaNodeLocation().value(),
                    sagaNode.sagaNodePhotos() == null ? null : sagaNode.sagaNodePhotos().values(),
                    sagaNode.sagaNodeTime() == null ? null : sagaNode.sagaNodeTime().value(),
                    sagaNode.milestone(),
                    sagaNodeDetail.favorited()
            );
        }
    }

    public record ApiResponse<T>(
            int code,
            T data,
            String message
    ) {

        static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(200, data, "success");
        }
    }
}

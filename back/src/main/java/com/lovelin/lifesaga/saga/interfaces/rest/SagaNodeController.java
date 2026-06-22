package com.lovelin.lifesaga.saga.interfaces.rest;

import com.lovelin.lifesaga.saga.application.command.AddSagaNodeCommand;
import com.lovelin.lifesaga.saga.application.command.DeleteSagaNodeCommand;
import com.lovelin.lifesaga.saga.application.command.ToggleSagaNodeFavoriteCommand;
import com.lovelin.lifesaga.saga.application.command.ToggleSagaNodeMilestoneCommand;
import com.lovelin.lifesaga.saga.application.command.UpdateSagaNodeCommand;
import com.lovelin.lifesaga.saga.application.service.AddSagaNodeApplicationService;
import com.lovelin.lifesaga.saga.application.service.DeleteSagaNodeApplicationService;
import com.lovelin.lifesaga.saga.application.service.SagaNodeQueryApplicationService;
import com.lovelin.lifesaga.saga.application.service.ToggleSagaNodeFavoriteApplicationService;
import com.lovelin.lifesaga.saga.application.service.ToggleSagaNodeMilestoneApplicationService;
import com.lovelin.lifesaga.saga.application.service.UpdateSagaNodeApplicationService;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
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
import java.util.Map;

@RestController
@RequestMapping("/api/sagas/{sagaId}/nodes")
public class SagaNodeController {

    private final SagaNodeQueryApplicationService sagaNodeQueryApplicationService;
    private final AddSagaNodeApplicationService addSagaNodeApplicationService;
    private final UpdateSagaNodeApplicationService updateSagaNodeApplicationService;
    private final ToggleSagaNodeMilestoneApplicationService toggleSagaNodeMilestoneApplicationService;
    private final ToggleSagaNodeFavoriteApplicationService toggleSagaNodeFavoriteApplicationService;
    private final DeleteSagaNodeApplicationService deleteSagaNodeApplicationService;

    public SagaNodeController(
            SagaNodeQueryApplicationService sagaNodeQueryApplicationService,
            AddSagaNodeApplicationService addSagaNodeApplicationService,
            UpdateSagaNodeApplicationService updateSagaNodeApplicationService,
            ToggleSagaNodeMilestoneApplicationService toggleSagaNodeMilestoneApplicationService,
            ToggleSagaNodeFavoriteApplicationService toggleSagaNodeFavoriteApplicationService,
            DeleteSagaNodeApplicationService deleteSagaNodeApplicationService
    ) {
        this.sagaNodeQueryApplicationService = sagaNodeQueryApplicationService;
        this.addSagaNodeApplicationService = addSagaNodeApplicationService;
        this.updateSagaNodeApplicationService = updateSagaNodeApplicationService;
        this.toggleSagaNodeMilestoneApplicationService = toggleSagaNodeMilestoneApplicationService;
        this.toggleSagaNodeFavoriteApplicationService = toggleSagaNodeFavoriteApplicationService;
        this.deleteSagaNodeApplicationService = deleteSagaNodeApplicationService;
    }

    @GetMapping
    public SagaController.ApiResponse<List<SagaNodeResponse>> list(
            @PathVariable Long sagaId,
            HttpServletRequest httpServletRequest
    ) {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(currentUserId(httpServletRequest));
        List<SagaNodeResponse> sagaNodes = sagaNodeQueryApplicationService.listSagaNodes(
                        new SagaId(sagaId),
                        sagaOwnerId
                ).stream()
                .map(SagaNodeResponse::from)
                .toList();
        return SagaController.ApiResponse.success(sagaNodes);
    }

    @PostMapping
    public SagaController.ApiResponse<SagaNodeResponse> create(
            @PathVariable Long sagaId,
            @RequestBody SagaNodeRequest sagaNodeRequest,
            HttpServletRequest httpServletRequest
    ) {
        SagaNode sagaNode = addSagaNodeApplicationService.addSagaNode(new AddSagaNodeCommand(
                new SagaId(sagaId),
                new SagaOwnerId(currentUserId(httpServletRequest)),
                new SagaNodeTitle(sagaNodeRequest.title()),
                new SagaNodeOrder(sagaNodeRequest.sortOrder()),
                toSagaNodeDescription(sagaNodeRequest.description()),
                new SagaNodeLocation(sagaNodeRequest.location()),
                toSagaNodePhotos(sagaNodeRequest.photos()),
                new SagaNodeTime(sagaNodeRequest.nodeTime())
        ));
        return SagaController.ApiResponse.success(SagaNodeResponse.from(sagaNode, false));
    }

    @GetMapping("/{id}")
    public SagaController.ApiResponse<SagaNodeResponse> detail(
            @PathVariable Long sagaId,
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        SagaNodeQueryApplicationService.SagaNodeDetail sagaNodeDetail = sagaNodeQueryApplicationService.getSagaNode(
                new SagaId(sagaId),
                new SagaNodeId(id),
                new SagaOwnerId(currentUserId(httpServletRequest))
        );
        return SagaController.ApiResponse.success(SagaNodeResponse.from(sagaNodeDetail));
    }

    @PutMapping("/{id}")
    public SagaController.ApiResponse<SagaNodeResponse> update(
            @PathVariable Long sagaId,
            @PathVariable Long id,
            @RequestBody SagaNodeRequest sagaNodeRequest,
            HttpServletRequest httpServletRequest
    ) {
        SagaNode sagaNode = updateSagaNodeApplicationService.updateSagaNode(new UpdateSagaNodeCommand(
                new SagaId(sagaId),
                new SagaNodeId(id),
                new SagaOwnerId(currentUserId(httpServletRequest)),
                new SagaNodeTitle(sagaNodeRequest.title()),
                new SagaNodeOrder(sagaNodeRequest.sortOrder()),
                toSagaNodeDescription(sagaNodeRequest.description()),
                new SagaNodeLocation(sagaNodeRequest.location()),
                toSagaNodePhotos(sagaNodeRequest.photos()),
                new SagaNodeTime(sagaNodeRequest.nodeTime()),
                Boolean.TRUE.equals(sagaNodeRequest.milestone())
        ));
        return SagaController.ApiResponse.success(SagaNodeResponse.from(sagaNode, false));
    }

    @PutMapping("/{id}/toggle-milestone")
    public SagaController.ApiResponse<SagaNodeResponse> toggleMilestone(
            @PathVariable Long sagaId,
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        SagaNode sagaNode = toggleSagaNodeMilestoneApplicationService.toggleSagaNodeMilestone(
                new ToggleSagaNodeMilestoneCommand(
                        new SagaId(sagaId),
                        new SagaNodeId(id),
                        new SagaOwnerId(currentUserId(httpServletRequest))
                )
        );
        return SagaController.ApiResponse.success(SagaNodeResponse.from(sagaNode, false));
    }

    @PutMapping("/{id}/favorite")
    public SagaController.ApiResponse<Map<String, Boolean>> toggleFavorite(
            @PathVariable Long sagaId,
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        boolean favorited = toggleSagaNodeFavoriteApplicationService.toggleFavorite(new ToggleSagaNodeFavoriteCommand(
                new SagaId(sagaId),
                new SagaNodeId(id),
                new SagaOwnerId(currentUserId(httpServletRequest))
        ));
        return SagaController.ApiResponse.success(Map.of("favorited", favorited));
    }

    @DeleteMapping("/{id}")
    public SagaController.ApiResponse<Void> delete(
            @PathVariable Long sagaId,
            @PathVariable Long id,
            HttpServletRequest httpServletRequest
    ) {
        deleteSagaNodeApplicationService.deleteSagaNode(new DeleteSagaNodeCommand(
                new SagaId(sagaId),
                new SagaNodeId(id),
                new SagaOwnerId(currentUserId(httpServletRequest))
        ));
        return SagaController.ApiResponse.success(null);
    }

    private Long currentUserId(HttpServletRequest httpServletRequest) {
        Object userId = httpServletRequest.getAttribute("userId");
        if (!(userId instanceof Long value)) {
            throw new IllegalStateException("未登录");
        }
        return value;
    }

    private SagaNodeDescription toSagaNodeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return new SagaNodeDescription(description);
    }

    private SagaNodePhotos toSagaNodePhotos(List<String> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return new SagaNodePhotos(photos);
    }

    public record SagaNodeRequest(
            String title,
            int sortOrder,
            String description,
            String location,
            List<String> photos,
            LocalDateTime nodeTime,
            Boolean milestone
    ) {
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

        static SagaNodeResponse from(SagaNodeQueryApplicationService.SagaNodeDetail sagaNodeDetail) {
            return from(sagaNodeDetail.sagaNode(), sagaNodeDetail.favorited());
        }

        static SagaNodeResponse from(SagaNode sagaNode, boolean favorited) {
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
                    favorited
            );
        }
    }
}

package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.service.NodeService;
import com.lovelin.lifesaga.service.SagaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SagaControllerTest {

    @Test
    void completeReturns403WhenSagaDoesNotBelongToUser() {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);

        FakeSagaService sagaService = new FakeSagaService(saga);
        SagaController controller = new SagaController(sagaService, new FakeNodeService());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 100L);

        Map<String, Object> response = controller.complete(12L, request);

        assertEquals(403, response.get("code"));
        assertEquals("无权修改", response.get("message"));
        assertEquals(0, sagaService.completeCalls);
    }

    @Test
    void completeCallsServiceForOwner() {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);

        FakeSagaService sagaService = new FakeSagaService(saga);
        SagaController controller = new SagaController(sagaService, new FakeNodeService());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 99L);

        Map<String, Object> response = controller.complete(12L, request);

        assertEquals(200, response.get("code"));
        assertEquals("success", response.get("message"));
        assertEquals(1, sagaService.completeCalls);
        assertEquals(12L, sagaService.completedId);
    }

    @Test
    void publicListKeepsPublicFlagButOmitsUserId() throws Exception {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);
        saga.setName("公开副本");
        saga.setPublic(true);

        FakeSagaService sagaService = new FakeSagaService(saga);
        sagaService.publicSagas = List.of(saga);
        SagaController controller = new SagaController(sagaService, new FakeNodeService());

        Map<String, Object> response = controller.publicList();
        String json = new ObjectMapper().writeValueAsString(response.get("data"));

        assertEquals(200, response.get("code"));
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"isPublic\":true"));
        org.junit.jupiter.api.Assertions.assertFalse(json.contains("userId"));
    }

    @Test
    void detailAllowsNonOwnerWhenSagaIsPublic() {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);
        saga.setPublic(true);

        FakeSagaService sagaService = new FakeSagaService(saga);
        SagaController controller = new SagaController(sagaService, new FakeNodeService());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 100L);

        Map<String, Object> response = controller.detail(12L, request);
        assertEquals(200, response.get("code"));
    }

    @Test
    void detailRejectsNonOwnerWhenSagaIsPrivate() {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);
        saga.setPublic(false);

        FakeSagaService sagaService = new FakeSagaService(saga);
        SagaController controller = new SagaController(sagaService, new FakeNodeService());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 100L);

        Map<String, Object> response = controller.detail(12L, request);
        assertEquals(403, response.get("code"));
        assertTrue(String.valueOf(response.get("message")).contains("无权访问"));
    }

    private static final class FakeSagaService extends SagaService {
        private final Saga saga;
        private int completeCalls = 0;
        private Long completedId;
        private List<Saga> publicSagas = List.of();

        private FakeSagaService(Saga saga) {
            super(null, null, null, null);
            this.saga = saga;
        }

        @Override
        public Saga getById(Long id) {
            return saga;
        }

        @Override
        public Saga complete(Long id) {
            completeCalls++;
            completedId = id;
            return saga;
        }

        @Override
        public List<Saga> listPublic() {
            return publicSagas;
        }
    }

    private static final class FakeNodeService extends NodeService {
        private FakeNodeService() {
            super(null, null, null, null);
        }

        @Override
        public List<com.lovelin.lifesaga.model.SagaNode> listBySagaId(Long sagaId, Long userId) {
            return List.of();
        }
    }
}

package de.ips.creactivities.chatbot.process;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CamundaServiceTest {

    private CamundaService camundaService;

    private MockCamundaRuntimeService mockRuntimeService;

    @BeforeEach
    public void init() {
        mockRuntimeService = new MockCamundaRuntimeService();
        camundaService = new CamundaService();
        camundaService.setRuntimeService(mockRuntimeService);
    }

    @Test
    public void testHandleCamundaEventWithoutVariables() {
        camundaService.handleCamundaEvent("message1", "process1", "");

        Assertions.assertEquals(1, mockRuntimeService.getCorrelatedMessages().size());
        Assertions.assertTrue(mockRuntimeService.getCorrelatedMessages().contains("message1"));
        Assertions.assertTrue(mockRuntimeService.getProcessVariables().isEmpty());
    }

    @Test
    public void testHandleCamundaEventWithVariables() {
        camundaService.handleCamundaEvent("message1", "process1", "_variable1_value1");

        Assertions.assertEquals(1, mockRuntimeService.getCorrelatedMessages().size());
        Assertions.assertTrue(mockRuntimeService.getCorrelatedMessages().contains("message1"));

        Assertions.assertEquals(1, mockRuntimeService.getProcessVariables().size());
        Assertions.assertTrue(mockRuntimeService.getProcessVariables().containsKey("variable1"));
        Assertions.assertEquals("value1", mockRuntimeService.getProcessVariables().get("variable1"));

    }
}

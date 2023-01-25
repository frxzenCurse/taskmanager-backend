package training.taskManager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import training.taskManager.model.FilesInfo;
import training.taskManager.service.FilesInfoService;
import training.taskManager.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilesInfoController.class)
@WithMockUser
class FilesInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilesInfoService filesInfoService;
    @MockBean
    private UserService userService;
    private final Long id = 1L;

    @Test
    void itShouldDownloadFile() throws Exception {
        FilesInfo filesInfo = FilesInfo.builder()
                .id(id)
                .name("Test file")
                .build();
        when(filesInfoService.findFile(id)).thenReturn(filesInfo);

        ResultActions resultActions = mockMvc.perform(get("/files/download/" + id));

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();
        verify(filesInfoService).findFile(id);
        verify(filesInfoService).downloadFile(any());
        assertThat(result.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertThat(result.getResponse().getHeader("Content-Disposition")).contains(filesInfo.getName());
    }

    @Test
    void itShouldDelete() throws Exception {
        String message = "Файл успешно удален";
        when(filesInfoService.deleteFile(id)).thenReturn(message);

        ResultActions resultActions = mockMvc.perform(
                post("/files/delete/" + id)
                        .with(csrf())
                        .contentType("application/json")
        );

        String result = resultActions
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        verify(filesInfoService).deleteFile(id);
        assertThat(result).isEqualTo(message);
    }
}
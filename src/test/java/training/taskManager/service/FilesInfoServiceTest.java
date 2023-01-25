package training.taskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import training.taskManager.exceptions.FileNotFoundException;
import training.taskManager.model.FilesInfo;
import training.taskManager.model.Task;
import training.taskManager.repo.FilesInfoRepo;
import training.taskManager.service.utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilesInfoServiceTest {

    @Mock private FilesInfoRepo filesInfoRepo;
    @Mock private FileManager fileManager;
    private FilesInfoService underTest;

    @BeforeEach
    void setUp() {
        underTest = new FilesInfoService(filesInfoRepo, fileManager);
    }

    @Test
    void canSaveFiles() {
        List<MultipartFile> files = new ArrayList<>();
        MockMultipartFile firstFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, world!".getBytes()
        );
        MockMultipartFile secondFile = new MockMultipartFile(
                "test",
                "writingTests.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, tests!".getBytes()
        );
        files.add(firstFile);
        files.add(secondFile);
        FilesInfoService filesInfoService = Mockito.spy(new FilesInfoService(filesInfoRepo, fileManager));

        filesInfoService.saveFiles(new Task(), files);

        verify(filesInfoService, times(files.size())).save(any(), any());
    }

    @Test
    void canSaveFile() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, world!".getBytes()
        );
        Task task = new Task();

        underTest.save(task, file);

        ArgumentCaptor<FilesInfo> argumentCaptor = ArgumentCaptor.forClass(FilesInfo.class);
        verify(fileManager).generateKey(file.getName());
        verify(fileManager).upload(any(), any());
        verify(filesInfoRepo).save(argumentCaptor.capture());
        FilesInfo capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue.getName()).isEqualTo(file.getOriginalFilename());
        assertThat(capturedValue.getSize()).isEqualTo(file.getSize());
    }

    @Test
    void canFindFileById() {
        Long id = 1L;
        when(filesInfoRepo.findById(id)).thenReturn(Optional.of(new FilesInfo()));

        underTest.findFile(id);

        verify(filesInfoRepo).findById(id);
    }
    @Test
    void willThrowFileNotFoundById() {
        Long id = 1L;
        when(filesInfoRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findFile(id))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessageContaining("Файл с id " + id + " не был найден");

        verify(filesInfoRepo).findById(id);
    }

    @Test
    void canDownloadFile() throws IOException {

        underTest.downloadFile(new FilesInfo());

        verify(fileManager).download(any());
    }

    @Test
    void canDeleteFile() throws IOException {
        String message = "Файл успешно удален";
        Long id = 1L;
        when(filesInfoRepo.findById(id)).thenReturn(Optional.of(new FilesInfo()));

        String expected = underTest.deleteFile(id);

        verify(filesInfoRepo).findById(id);
        verify(fileManager).delete(any());
        verify(filesInfoRepo).delete(any());
        assertThat(expected).isEqualTo(message);
    }
}
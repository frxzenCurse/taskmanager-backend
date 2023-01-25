package training.taskManager.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileManagerTest {

    @Autowired
    private FileManager underTest;

    private final Path PATH = Path.of("C:\\test\\fileManagerTest");

    @BeforeEach
    void setUp() {
        underTest = new FileManager();
    }

    @Test
    void canUpload() throws IOException {
        InputStream stream = new ByteArrayInputStream("Hello, tests".getBytes());


        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
            MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
        ) {
            mockedPaths.when(() -> Paths.get(any(), any()))
                            .thenReturn(PATH);
            mockedFiles.when(() -> Files.createFile(any(), any()))
                            .thenReturn(PATH);

            underTest.upload(stream, "string key");

            mockedPaths.verify(() -> Paths.get(any(), any()));
            mockedFiles.verify(() -> Files.createFile(any()));
        }

        Files.delete(PATH);
    }

    @Test
    void canGenerateKey() {
        try (MockedStatic<LocalDateTime> mockedPaths = Mockito.mockStatic(LocalDateTime.class);
             MockedStatic<DigestUtils> mockedFiles = Mockito.mockStatic(DigestUtils.class);
        ) {
            String key = "Generate key";

            String expected = underTest.generateKey(key);

            String result = key + null;
            mockedPaths.verify(LocalDateTime::now);
            mockedFiles.verify(() -> DigestUtils.md5DigestAsHex(result.getBytes()));
            assertThat(expected).isEqualTo(DigestUtils.md5DigestAsHex(result.getBytes()));
        }
    }

    @Test
    void canDownloadFile() throws IOException {
        Path file = Files.createFile(PATH);
        byte[] data = "Hello tests".getBytes();

        try (OutputStream outputStream = new FileOutputStream(file.toString())) {

            outputStream.write(data);
        }

        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(any(), any()))
                    .thenReturn(PATH);

            Resource expected = underTest.download("test");

            InputStream stream = expected.getInputStream();
            mockedPaths.verify(() -> Paths.get(any(), any()));
            assertThat(stream.readAllBytes()).isEqualTo(data);
            stream.close();
        }

        Files.delete(PATH);
    }

    @Test
    void canDeleteFile() throws IOException {
        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)
        ) {
            Files.createFile(PATH);

            mockedPaths.when(() -> Paths.get(any(), any()))
                    .thenReturn(PATH);

            underTest.delete("test");

            mockedPaths.verify(() -> Paths.get(any(), any()));
            mockedFiles.verify(() -> Files.delete(any()));
            assertThat(Files.exists(PATH)).isFalse();
        }
    }
}
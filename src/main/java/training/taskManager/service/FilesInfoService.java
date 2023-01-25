package training.taskManager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import training.taskManager.exceptions.FileNotFoundException;
import training.taskManager.exceptions.InvalidFileException;
import training.taskManager.model.FilesInfo;
import training.taskManager.model.Task;
import training.taskManager.repo.FilesInfoRepo;
import training.taskManager.service.utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilesInfoService {

    private final FilesInfoRepo filesInfoRepo;
    private final FileManager fileManager;

    public List<FilesInfo> saveFiles(Task task, List<MultipartFile> files) {
        List<FilesInfo> fileList = new ArrayList<>();

        files.forEach(file -> fileList.add(save(task, file)));

        return fileList;
    }

    public FilesInfo save(Task task, MultipartFile attachment) {
        String key = fileManager.generateKey(attachment.getName());

        FilesInfo createdFile = FilesInfo.builder()
                .name(attachment.getOriginalFilename())
                .fileKey(key)
                .size(attachment.getSize())
                .task(task)
                .build();
        try {
            fileManager.upload(attachment.getInputStream(), key);
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }

        return filesInfoRepo.save(createdFile);
    }

    public FilesInfo findFile(Long fileId) {
        return filesInfoRepo.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Файл с id " + fileId + " не был найден"));
    }

    public Resource downloadFile(FilesInfo filesInfo) throws IOException {
        return fileManager.download(filesInfo.getFileKey());
    }

    public String deleteFile(Long fileId) {
        FilesInfo file = findFile(fileId);
        try {
            fileManager.delete(file.getFileKey());
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
        filesInfoRepo.delete(file);

        return "Файл успешно удален";
    }
}

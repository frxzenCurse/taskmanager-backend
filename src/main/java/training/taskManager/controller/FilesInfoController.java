package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import training.taskManager.model.FilesInfo;
import training.taskManager.service.FilesInfoService;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesInfoController {

    private final FilesInfoService filesInfoService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) throws IOException {
        FilesInfo file = filesInfoService.findFile(fileId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(filesInfoService.downloadFile(file));
    }

    @PostMapping("/delete/{fileId}")
    public ResponseEntity<String> delete(@PathVariable Long fileId) {
        return ResponseEntity.ok(filesInfoService.deleteFile(fileId));
    }
}

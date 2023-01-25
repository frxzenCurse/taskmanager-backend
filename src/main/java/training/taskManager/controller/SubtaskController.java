package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import training.taskManager.dto.SubtaskDto;
import training.taskManager.model.Subtask;
import training.taskManager.service.SubtaskService;
import training.taskManager.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/subtasks")
@RequiredArgsConstructor
public class SubtaskController {
    private final SubtaskService subtaskService;

    @PostMapping("/add")
    public ModelAndView addSubtask(@RequestBody SubtaskDto subtaskDto) {
        Subtask subtask = subtaskService.save(subtaskDto);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("subtask", subtask);

        return new ModelAndView("partial/subtask.html", modelMap);
    }

    @PostMapping("/delete/{subtaskId}")
    public ResponseEntity<String> deleteSubtask(@PathVariable Long subtaskId) {
        return ResponseEntity.ok(subtaskService.deleteSubtask(subtaskId));
    }
}

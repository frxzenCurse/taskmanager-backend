package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import training.taskManager.model.Workspace;
import training.taskManager.service.WorkspaceService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @PostMapping("/add")
    public ModelAndView addWorkspace(@RequestBody Workspace workspace) {
        workspace = workspaceService.addWorkspace(workspace);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("workspace", workspace);

        return new ModelAndView("partial/workspace-link.html", modelMap);
    }

    @PostMapping("/delete/{workspaceId}")
    public void deleteWorkspace(@PathVariable Long workspaceId, HttpServletResponse response) throws IOException {
        workspaceService.deleteWorkspace(workspaceId);

        response.sendRedirect("/about");
    }

    @PostMapping("/exit/{workspaceId}")
    public void workspaceExit(@PathVariable Long workspaceId, HttpServletResponse response) throws IOException {
        workspaceService.workspaceExit(workspaceId);

        response.sendRedirect("/about");
    }
}

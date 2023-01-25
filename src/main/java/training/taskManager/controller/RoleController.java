package training.taskManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import training.taskManager.dto.RoleDto;
import training.taskManager.model.Role;
import training.taskManager.service.RoleService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/add")
    public ModelAndView addRole(@RequestBody RoleDto roleDto) {
        Role role = roleService.addRole(roleDto);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("role", role);

        return new ModelAndView("partial/role.html", modelMap);
    }

    @PostMapping("/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }
}

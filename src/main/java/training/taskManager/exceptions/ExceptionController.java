package training.taskManager.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import training.taskManager.model.User;
import training.taskManager.service.UserService;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController extends ResponseEntityExceptionHandler {

    private final UserService userService;

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<String> notFoundExceptionHandler(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RoleNotFoundException.class, WorkspaceNotFoundException.class})
    public String notFoundRedirectHandler(Exception ex, Model model) {
        User user = userService.getCurrentUser();

        model.addAttribute("user", user);
        model.addAttribute("message", ex.getMessage());

        return "404";
    }
}

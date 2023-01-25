package training.taskManager.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Roles {
    EXECUTOR("Исполнитель"),
    OBSERVER("Наблюдатель"),
    FULL_AUTHORITIES("Полные права"),
    OWNER("Владелец");

    private final String value;
}

package training.taskManager.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    COMPLETE("Выполнена"),
    GOING("В процессе"),
    PLANNED("Запланирована");

    private final String value;
}

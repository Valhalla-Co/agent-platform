package br.com.valhalla.agentcore.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representando o identificador de uma Task.
 */
public class TaskId {

    private final String value;

    private TaskId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TaskId cannot be null or blank");
        }
        this.value = value;
    }

    public static TaskId of(String value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId = (TaskId) o;
        return Objects.equals(value, taskId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

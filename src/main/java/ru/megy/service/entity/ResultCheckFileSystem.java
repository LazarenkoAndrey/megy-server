package ru.megy.service.entity;

public class ResultCheckFileSystem {
    private boolean result;
    private String message;

    public ResultCheckFileSystem(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultCheckFileSystem that = (ResultCheckFileSystem) o;

        if (result != that.result) return false;
        return !(message != null ? !message.equals(that.message) : that.message != null);

    }

    @Override
    public int hashCode() {
        int result1 = (result ? 1 : 0);
        result1 = 31 * result1 + (message != null ? message.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "ResultCheckFileSystem{" +
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}

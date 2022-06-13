package ru.alishev.springcourse.FirstRestApp.util;

public class PersonNotCeatedFoundException extends RuntimeException {
    public PersonNotCeatedFoundException(String msg) {
        super(msg);
    }
}

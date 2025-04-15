package com.example.task_mng_opakovanie.domain;

public enum TaskStatus {
    NEW,
    DONE;

    public static TaskStatus toEnum(String s){
        switch (s){
            case "NEW" -> {
                return NEW;
            }
            case "DONE" -> {
                return DONE;
            }
            default -> throw new IllegalArgumentException("Unknown task status: " + s);
        }

    }
}

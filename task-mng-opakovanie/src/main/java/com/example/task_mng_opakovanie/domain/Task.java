package com.example.task_mng_opakovanie.domain;

import lombok.Value;

import java.time.OffsetDateTime;

@Value
public class Task {
    long id;
    long user_id;
    Long project_id;
    String name;
    String description;
    TaskStatus status;
    OffsetDateTime createdAt;

}

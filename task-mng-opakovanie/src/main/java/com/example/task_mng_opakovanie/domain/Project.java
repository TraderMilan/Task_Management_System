package com.example.task_mng_opakovanie.domain;

import lombok.Value;

import java.time.OffsetDateTime;

@Value
public class Project {
    long id;
    long user_id;
    String name;
    String description;
    OffsetDateTime created_at;

}

package com.example.task_mng_opakovanie.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAddRequest {
    private long user_id;
    private Long project_id;
    private String name;
    private String description;
}

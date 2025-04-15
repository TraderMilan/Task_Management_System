package com.example.task_mng_opakovanie.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAddRequest {
    private Long user_id;
    private String name;
    private String description;

}

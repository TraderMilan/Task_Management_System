package com.example.task_mng_opakovanie.api.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEditRequest {
    private String name;
    private String description;
}

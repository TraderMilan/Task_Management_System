package com.example.task_mng_opakovanie.api.request;

import com.example.task_mng_opakovanie.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusEditRequest {
    private TaskStatus status;
}

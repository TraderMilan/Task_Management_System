package com.example.task_mng_opakovanie.implementation.jdbc.mapper;

import com.example.task_mng_opakovanie.domain.Task;
import com.example.task_mng_opakovanie.domain.TaskStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;

@Component
public class TaskRowMapper implements RowMapper<Task> {
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getObject("project_id") != null ? rs.getLong("project_id") : null,
                rs.getString("name"),
                rs.getString("description"),
                TaskStatus.toEnum(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime().atOffset(ZoneOffset.UTC)
        );
    }
}

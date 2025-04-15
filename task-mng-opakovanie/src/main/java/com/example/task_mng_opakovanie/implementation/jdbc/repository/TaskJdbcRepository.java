package com.example.task_mng_opakovanie.implementation.jdbc.repository;

import com.example.task_mng_opakovanie.api.exception.InternalErrorException;
import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.TaskAddRequest;
import com.example.task_mng_opakovanie.api.request.TaskEditRequest;
import com.example.task_mng_opakovanie.domain.Task;
import com.example.task_mng_opakovanie.domain.TaskStatus;
import com.example.task_mng_opakovanie.implementation.jdbc.mapper.TaskRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class TaskJdbcRepository {
    private final TaskRowMapper rowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final static Logger logger;
    private final static String GET_ALL;
    private final static String GET_BY_ID;
    private final static String GET_ALL_BY_PROJECT;
    private final static String GET_ALL_BY_USER;
    private final static String ADD;
    private final static String DELETE;
    private final static String EDIT;
    private final static String EDIT_STATUS;
    private final static String ASSIGN_TO_PROJECT;
    private final static String DELETE_ALL_BY_USER;
    private final static String DELETE_ALL_BY_PROJECT;


    static {
        logger = LoggerFactory.getLogger(TaskJdbcRepository.class);
        GET_ALL = "SELECT * FROM task";
        GET_BY_ID = "SELECT * FROM task WHERE id = ?";
        GET_ALL_BY_PROJECT = "SELECT * FROM task WHERE project_id = ?";
        GET_ALL_BY_USER = "SELECT * FROM task WHERE user_id = ?";
        ADD = "INSERT into task (id, user_id, project_id, name, description, status, created_at) " +
                "VALUES (next value for task_id_seq, ?, ?, ?, ?, ?, ?)";
        DELETE = "DELETE FROM task WHERE id = ?";
        EDIT = "UPDATE task SET name = ?, description = ?, status = ? WHERE id = ?";
        EDIT_STATUS = "UPDATE task SET status = ? WHERE id = ?";
        ASSIGN_TO_PROJECT = "UPDATE task SET project_id = ? WHERE id = ?";
        DELETE_ALL_BY_USER = "DELETE FROM task WHERE user_id = ?";
        DELETE_ALL_BY_PROJECT = "DELETE FROM task WHERE project_id = ?";

    }

    public TaskJdbcRepository(TaskRowMapper rowMapper, JdbcTemplate jdbcTemplate) {
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByUser(long userId){
        try{
            jdbcTemplate.update(DELETE_ALL_BY_USER, userId);
        } catch (DataAccessException e){
            logger.error("Error while deleting tasks by userId", e);
            throw new InternalErrorException("Error while deleting tasks by userId");
        }
    }

    public void deleteByProject(long projectId){
        try {
            jdbcTemplate.update(DELETE_ALL_BY_PROJECT, projectId);
        } catch (DataAccessException e){
            logger.error("Error while deleting tasks by userId", e);
            throw new InternalErrorException("Error while deleting tasks by userId");
        }
    }


    public void assignProject(long task_id, long project_id){
        try {
            jdbcTemplate.update(ASSIGN_TO_PROJECT, project_id, task_id);
        } catch (DataAccessException e) {
            logger.error("Error while editing task.", e);
            throw new InternalErrorException("Error while editing task");
        }
    }


    public void updateStatus(long id, TaskStatus status) {
        try {
            jdbcTemplate.update(EDIT_STATUS, status.toString(), id);
        } catch (DataAccessException e) {
            logger.error("Error while editing task.", e);
            throw new InternalErrorException("Error while editing task");
        }
    }


    public void edit(long id, TaskEditRequest request) {
        try {
            jdbcTemplate.update(EDIT, request.getName(), request.getDescription(), request.getStatus().toString(), id);
        } catch (DataAccessException e) {
            logger.error("Error while editing task.", e);
            throw new InternalErrorException("Error while editing task");
        }
    }

    public void delete(long id) {
        try {
            jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException e) {
            logger.error("Error while getting task by id", e);
            throw new InternalErrorException("Error while getting task by id");
        }
    }

    public long add(TaskAddRequest request) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, request.getUser_id());
                if (request.getProject_id() == null) {
                    ps.setNull(2, Types.BIGINT);
                } else {
                    ps.setLong(2, request.getProject_id());
                }
                ps.setString(3, request.getName());
                if (request.getDescription() != null) {
                    ps.setString(4, request.getDescription());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                }
                ps.setString(5, TaskStatus.NEW.toString());
                ps.setTimestamp(6, Timestamp.from(OffsetDateTime.now().toInstant()));

                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                throw new InternalErrorException("Key holder is null");
            }
            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            logger.error("Error while adding task", e);
            throw new InternalErrorException("Error while adding task");
        }

    }


    public List<Task> getAllByUser(long user_id) {
        try {
            return jdbcTemplate.query(GET_ALL_BY_USER, rowMapper, user_id);
        } catch (DataAccessException e) {
            logger.error("Error while getting tasks by user id", e);
            throw new InternalErrorException("Error while getting tasks by user id");
        }
    }


    public List<Task> getAllByProject(long project_id) {
        try {
            return jdbcTemplate.query(GET_ALL_BY_PROJECT, rowMapper, project_id);
        } catch (DataAccessException e) {
            logger.error("Error while getting tasks by project id", e);
            throw new InternalErrorException("Error while getting tasks by project id");
        }
    }

    public Task getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFound("Task with " + id + " was not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting task by id", e);
            throw new InternalErrorException("Error while getting task by id");
        }
    }


    public List<Task> getAll() {
        try {
            return jdbcTemplate.query(GET_ALL, rowMapper);
        } catch (DataAccessException e) {
            logger.error("Error while getting tasks", e);
            throw new InternalErrorException("Error while getting tasks");
        }
    }

}

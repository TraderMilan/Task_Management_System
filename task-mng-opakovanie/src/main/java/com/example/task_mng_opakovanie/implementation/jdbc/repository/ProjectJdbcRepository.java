package com.example.task_mng_opakovanie.implementation.jdbc.repository;

import com.example.task_mng_opakovanie.api.exception.BadRequestException;
import com.example.task_mng_opakovanie.api.exception.InternalErrorException;
import com.example.task_mng_opakovanie.api.request.ProjectAddRequest;
import com.example.task_mng_opakovanie.api.request.ProjectEditRequest;
import com.example.task_mng_opakovanie.domain.Project;
import com.example.task_mng_opakovanie.implementation.jdbc.mapper.ProjectRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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
public class ProjectJdbcRepository {
    private final ProjectRowMapper mapper;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger;
    private static final String GET_ALL;
    private static final String GET_BY_ID;
    private static final String GET_ALL_BY_USER_ID;
    private static final String ADD_PROJECT;
    private static final String UPDATE;
    private static final String DELETE;
    private static final String DELETE_BY_USER;

    public ProjectJdbcRepository(ProjectRowMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    static {
        logger = LoggerFactory.getLogger(ProjectJdbcRepository.class);
        GET_ALL = "SELECT * FROM project";
        GET_BY_ID = "SELECT * FROM project WHERE id = ?";
        GET_ALL_BY_USER_ID = "SELECT * FROM project WHERE user_id = ?";
        ADD_PROJECT = "INSERT into project (id, user_id, name, description, created_at) VALUES (next value for project_id_seq, ?, ?, ?, ?)";
        UPDATE = "UPDATE project SET name = ?, description = ? WHERE id = ?";
        DELETE = "DELETE from project WHERE id = ?";
        DELETE_BY_USER = "DELETE from project WHERE user_id = ?";
    }

    public void DeleteByUser(long userId){
        try {
            jdbcTemplate.update(DELETE_BY_USER, userId);
        } catch (DataAccessException e){
            logger.error("Error while deleting project by user id", e);
            throw new InternalErrorException("Error while deleting project by user id");
        }
    }


    public void delete(long id) {
        try {
            jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException e) {
            logger.error("Error while deleting project", e);
            throw new InternalErrorException("Error while deleting project");
        }
    }

    public void update(long id, ProjectEditRequest request) {
        try {
            jdbcTemplate.update(UPDATE, request.getName(), request.getDescription(), id);
        } catch (DataAccessException e) {
            logger.error("Error while editing project", e);
            throw new InternalErrorException("Error while editing project");
        }
    }

    public long createProject(ProjectAddRequest request) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement(ADD_PROJECT, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, request.getUser_id());
                ps.setString(2, request.getName());
                if (request.getDescription() != null) {
                    ps.setString(3, request.getDescription());
                } else {
                    ps.setNull(3, Types.VARCHAR);
                }
                ps.setTimestamp(4, Timestamp.from(OffsetDateTime.now().toInstant()));
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() == null) {
                logger.error("Key holder is null");
                throw new InternalErrorException("Error while adding project");
            }

            return keyHolder.getKey().longValue();

        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Error while adding project");
        } catch (DataAccessException e) {
            logger.error("Error while adding project", e);
            throw new InternalErrorException("Error while adding user");

        }


    }

    public Project getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException("Project with id " + id + " does not exists");
        } catch (DataAccessException e) {
            logger.error("Error while getting project", e);
            throw new InternalErrorException("Error while getting project");
        }
    }

    public List<Project> getAllByUserId(long userId) {
        try {
            return jdbcTemplate.query(GET_ALL_BY_USER_ID, mapper, userId);
        } catch (DataAccessException e) {
            logger.error("Error while getting project", e);
            throw new InternalErrorException("Error while getting project");
        }
    }

    public List<Project> getAll() {
        try {
            return jdbcTemplate.query(GET_ALL, mapper);
        } catch (DataAccessException e) {
            logger.error("Error while getting projects", e);
            throw new InternalErrorException("Error while getting projects");
        }
    }


}

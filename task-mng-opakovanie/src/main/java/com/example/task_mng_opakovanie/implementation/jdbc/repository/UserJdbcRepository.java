package com.example.task_mng_opakovanie.implementation.jdbc.repository;

import com.example.task_mng_opakovanie.api.exception.BadRequestException;
import com.example.task_mng_opakovanie.api.exception.InternalErrorException;
import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.UserAddRequest;
import com.example.task_mng_opakovanie.domain.User;
import com.example.task_mng_opakovanie.implementation.jdbc.mapper.UserRowMapper;
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
import java.util.List;

@Repository
public class UserJdbcRepository {
    private final UserRowMapper mapper;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger;
    private static final String GET_ALL;
    private static final String GET_BY_ID;
    private static final String ADD_USER;
    private static final String DELETE_USER;

    static {
        logger = LoggerFactory.getLogger(UserJdbcRepository.class);
        GET_ALL = "SELECT * from user";
        GET_BY_ID = "SELECT * from user WHERE id = ?";
        ADD_USER = "INSERT into user (id, name, email) VALUES (next value for user_id_seq, ?, ?)";
        DELETE_USER = "DELETE from user WHERE id = ?";
    }

    public UserJdbcRepository(UserRowMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;

    }

    public void deleteUser(long id){
        try {
            jdbcTemplate.update(DELETE_USER, id);
        } catch (DataAccessException e){
            logger.error("Error while deleting user", e);
            throw new InternalErrorException("error while deleting user");
        }
    }

    public long addUser(UserAddRequest request) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement(ADD_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getName());
                ps.setString(2, request.getEmail());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() == null) {
                logger.error("KeyHolder is null");
                throw new InternalErrorException("Error while getting user");
            }

            return keyHolder.getKey().longValue();

        } catch (DataIntegrityViolationException e){
            throw new BadRequestException("user with email: " + request.getEmail() + " already exists");
        } catch (DataAccessException e){
            logger.error("Error while adding user", e);
            throw new InternalErrorException("Error while adding user");
        }

    }

    public User getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFound("user with id " + id + " was not found");
        } catch (DataAccessException e) {
            logger.error("Error while getting user by id", e);
            throw new InternalErrorException("Error while getting user");

        }
    }

    public List<User> getAll() {

        return jdbcTemplate.query(GET_ALL, mapper);
    }

}

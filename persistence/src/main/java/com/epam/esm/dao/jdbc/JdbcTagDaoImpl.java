package com.epam.esm.dao.jdbc;

import com.epam.esm.dao.TagDao;
import com.epam.esm.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTagDaoImpl implements TagDao {

    private static final String CREATE_ONE_SQL =
            "INSERT INTO tag(name) VALUES (?);";

    private static final String SELECT_ALL_SQL =
            "SELECT tag.id, tag.name FROM tag;";

    private static final String SELECT_ONE_SQL =
            "SELECT tag.id, tag.name FROM tag WHERE tag.id = ?;";

    private static final String DELETE_ONE_SQL =
            "DELETE FROM tag WHERE tag.id = ?;";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Tag> tagRowMapper;

    @Autowired
    public JdbcTagDaoImpl(JdbcTemplate jdbcTemplate,
                          RowMapper<Tag> tagRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = tagRowMapper;
    }

    @Override
    public boolean create(Tag tag) {
        return jdbcTemplate.update(CREATE_ONE_SQL, tag.getName()) == 1;
    }

    @Override
    public List<Tag> readAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, tagRowMapper);
    }

    @Override
    public Optional<Tag> readById(Long id) {
        List<Tag> tags = jdbcTemplate.query(SELECT_ONE_SQL, tagRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.uniqueResult(tags));
    }

    @Override
    public boolean update(Tag tag) {
        throw new UnsupportedOperationException("Update tag is not supported.");
    }

    @Override
    public boolean delete(Long id) {
        return jdbcTemplate.update(DELETE_ONE_SQL, id) > 0;
    }
}

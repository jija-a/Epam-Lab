package com.epam.esm.dao.jdbc;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.query.config.CertificateSelectQueryConfig;
import com.epam.esm.dao.query.config.CertificateUpdateQueryConfig;
import com.epam.esm.dao.query.impl.CertificateSelectQueryCreator;
import com.epam.esm.dao.query.impl.CertificateUpdateQueryCreator;
import com.epam.esm.domain.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCertificateDaoImpl implements CertificateDao {

    private static final String CREATE_ONE_SQL =
            "INSERT INTO gift_certificate(name, description, price, duration) VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT c.id, c.name, c.description, c.price, c.duration,"
                    + "c.create_date, c.last_update_date FROM gift_certificate c;";

    private static final String SELECT_ONE_SQL =
            "SELECT c.id, c.name, c.description, c.price, c.duration,"
                    + "c.create_date, c.last_update_date FROM gift_certificate c WHERE c.id = ?";

    private static final String DELETE_ONE_SQL =
            "DELETE FROM gift_certificate WHERE gift_certificate.id = ?";

    private static final String ATTACH_TAG_SQL =
            "INSERT INTO gift_certificate_tag(certificate_id, tag_id) VALUES (?, ?);";

    private static final String DETACH_TAG_SQL =
            "DELETE FROM gift_certificate_tag WHERE certificate_id = ? AND tag_id = ?;";

    private static final String SELECT_ONE_BY_NAME_SQL =
            "SELECT c.id, c.name, c.description, c.price, c.duration,"
                    + "c.create_date, c.last_update_date FROM gift_certificate c WHERE c.name = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Certificate> certificateRowMapper;

    @Autowired
    public JdbcCertificateDaoImpl(JdbcTemplate jdbcTemplate,
                                  RowMapper<Certificate> certificateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.certificateRowMapper = certificateRowMapper;
    }

    @Override
    public boolean create(Certificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        boolean isCreated = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(CREATE_ONE_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, certificate.getName());
            ps.setString(2, certificate.getDescription());
            ps.setBigDecimal(3, certificate.getPrice());
            ps.setShort(4, certificate.getDuration());
            return ps;
        }, keyHolder) == 1;
        certificate.setId(keyHolder.getKey().longValue());
        return isCreated;
    }

    @Override
    public List<Certificate> readAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, certificateRowMapper);
    }

    @Override
    public Optional<Certificate> readById(Long id) {
        List<Certificate> certificates =
                jdbcTemplate.query(SELECT_ONE_SQL, certificateRowMapper, id);
        return Optional.ofNullable(DataAccessUtils.uniqueResult(certificates));
    }

    @Override
    @Deprecated
    public boolean update(Certificate certificate) {
        throw new UnsupportedOperationException("Deprecated method, use update(config) instead.");
    }

    @Override
    public boolean update(CertificateUpdateQueryConfig config) {
        PreparedStatementCreator creator = new CertificateUpdateQueryCreator(config);
        return jdbcTemplate.update(creator) == 1;
    }

    @Override
    public boolean delete(Long id) {
        return jdbcTemplate.update(DELETE_ONE_SQL, id) == 1;
    }

    @Override
    public boolean attachTagToCertificate(long certificateId, long tagId) {
        return jdbcTemplate.update(ATTACH_TAG_SQL, certificateId, tagId) == 1;
    }

    @Override
    public boolean detachTagFromCertificate(long certificateId, long tagId) {
        return jdbcTemplate.update(DETACH_TAG_SQL, certificateId, tagId) == 1;
    }

    @Override
    public List<Certificate> query(CertificateSelectQueryConfig config) {
        PreparedStatementCreator creator = new CertificateSelectQueryCreator(config);
        return jdbcTemplate.query(creator, certificateRowMapper);
    }

    @Override
    public Optional<Certificate> readByName(String name) {
        List<Certificate> certificates =
                jdbcTemplate.query(SELECT_ONE_BY_NAME_SQL, certificateRowMapper, name);
        return Optional.ofNullable(DataAccessUtils.uniqueResult(certificates));
    }
}

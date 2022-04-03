package com.epam.esm.dao.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CertificateQueryCreator extends QueryCreator {

    private static final String SELECT_QUERY =
            "SELECT c.id, c.name, c.description, c.price, c.duration," +
            "c.create_date, c.last_update_date FROM gift_certificate c";

    private static final String TAG_CERTIFICATE_JOIN_QUERY =
            "gift_certificate_tag gct on c.id = gct.certificate_id";

    private static final String TAG_JOIN_QUERY =
            "tag t on t.id = gct.tag_id";

    private static final String TAG_NAME_QUERY =
            "t.name LIKE CONCAT('%', ? ,' %')";

    private static final String CERTIFICATE_NAME_QUERY =
            "c.name LIKE CONCAT('%', ? ,' %')";

    private static final String CERTIFICATE_DESCRIPTION_QUERY =
            "c.description LIKE CONCAT('%', ? ,' %')";

    private final CertificateQueryConfig config;

    public CertificateQueryCreator(CertificateQueryConfig config) {
        this.config = config;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        this.builder = new StringBuilder(SELECT_QUERY);
        PreparedStatement st = con.prepareStatement(this.buildQuery());
        this.setParameters(st);
        return st;
    }

    @Override
    protected String buildQuery() {
        if (config.getTagParam() != null) {
            this.attachTagQueryPart();
        }
        if (config.getSearchQuery() != null) {
            this.attachSearchQueryPart();
        }
        if (config.getParameterSortingTypeMap() != null) {
            this.attachSortingType();
        }
        return builder.toString();
    }

    @Override
    protected void setParameters(PreparedStatement st) throws SQLException {
        int index = 1;
        if (config.getTagParam() != null) {
            st.setObject(index++, config.getTagParam());
        }
        if (config.getSearchQuery() != null) {
            st.setObject(index++, config.getSearchQuery());
            st.setObject(index, config.getSearchQuery());
        }
    }

    public void attachSearchQueryPart() {
        if (defineWhereOrAnd().equals("WHERE")) {
            this.where(CERTIFICATE_NAME_QUERY);
        } else {
            this.and(CERTIFICATE_NAME_QUERY);
        }
        this.or(CERTIFICATE_DESCRIPTION_QUERY);
    }

    private void attachTagQueryPart() {
        this.join(TAG_CERTIFICATE_JOIN_QUERY, JoinType.INNER)
                .join(TAG_JOIN_QUERY, JoinType.INNER)
                .where(TAG_NAME_QUERY);
    }

    private void attachSortingType() {
        this.orderBy(config.getParameterSortingTypeMap());
    }

    private String defineWhereOrAnd() {
        if (hasWhereStatement) {
            return "AND";
        } else {
            return "WHERE";
        }
    }

}

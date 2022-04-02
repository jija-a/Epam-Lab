package com.epam.esm.dao;

import com.epam.esm.domain.Certificate;

public interface CertificateDao extends BaseDao<Certificate, Long> {

    boolean attachTagToCertificate(long certificateId, long tagId);

    boolean detachTagFromCertificate(long certificateId, long tagId);
}

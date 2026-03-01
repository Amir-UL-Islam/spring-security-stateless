package io.security.base.urls;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UrlsRepository extends JpaRepository<Urls, Long> {

    Page<Urls> findAllById(Long id, Pageable pageable);

    Urls findFirstByPrivilegeId(Long id);

    boolean existsByEndpointIgnoreCase(String endpoint);

}

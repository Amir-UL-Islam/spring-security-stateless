package io.security.base;

import io.security.base.events.BeforeDeletePrivilege;
import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.urls.Urls;
import io.security.base.urls.UrlsDTO;
import io.security.base.urls.UrlsRepository;
import io.security.base.util.NotFoundException;
import io.security.base.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UrlsService {

    private final UrlsRepository urlsRepository;
    private final PrivilegeRepository privilegeRepository;

    public UrlsService(final UrlsRepository urlsRepository,
            final PrivilegeRepository privilegeRepository) {
        this.urlsRepository = urlsRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public Page<UrlsDTO> findAll(final String filter, final Pageable pageable) {
        Page<Urls> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = urlsRepository.findAllById(longFilter, pageable);
        } else {
            page = urlsRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(urls -> mapToDTO(urls, new UrlsDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public UrlsDTO get(final Long id) {
        return urlsRepository.findById(id)
                .map(urls -> mapToDTO(urls, new UrlsDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UrlsDTO urlsDTO) {
        final Urls urls = new Urls();
        mapToEntity(urlsDTO, urls);
        return urlsRepository.save(urls).getId();
    }

    public void update(final Long id, final UrlsDTO urlsDTO) {
        final Urls urls = urlsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(urlsDTO, urls);
        urlsRepository.save(urls);
    }

    public void delete(final Long id) {
        final Urls urls = urlsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        urlsRepository.delete(urls);
    }

    private UrlsDTO mapToDTO(final Urls urls, final UrlsDTO urlsDTO) {
        urlsDTO.setId(urls.getId());
        urlsDTO.setEndpoint(urls.getEndpoint());
        urlsDTO.setMethod(urls.getMethod());
        urlsDTO.setPrivilege(urls.getPrivilege() == null ? null : urls.getPrivilege().getId());
        return urlsDTO;
    }

    private Urls mapToEntity(final UrlsDTO urlsDTO, final Urls urls) {
        urls.setEndpoint(urlsDTO.getEndpoint());
        urls.setMethod(urlsDTO.getMethod());
        final Privilege privilege = urlsDTO.getPrivilege() == null ? null : privilegeRepository.findById(urlsDTO.getPrivilege())
                .orElseThrow(() -> new NotFoundException("privilege not found"));
        urls.setPrivilege(privilege);
        return urls;
    }

    public boolean endpointExists(final String endpoint) {
        return urlsRepository.existsByEndpointIgnoreCase(endpoint);
    }

    @EventListener(BeforeDeletePrivilege.class)
    public void on(final BeforeDeletePrivilege event) {
        final ReferencedException referencedException = new ReferencedException();
        final Urls privilegeUrls = urlsRepository.findFirstByPrivilegeId(event.getId());
        if (privilegeUrls != null) {
            referencedException.setKey("privilege.urls.privilege.referenced");
            referencedException.addParam(privilegeUrls.getId());
            throw referencedException;
        }
    }

}

package io.security.base.urls;

import io.security.base.events.BeforeDeletePrivilege;
import io.security.base.util.NotFoundException;
import io.security.base.util.ReferencedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UrlsService {

    private final UrlsMapper urlsMapper;
    private final UrlsRepository urlsRepository;

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
                .map(urls -> urlsMapper.mapToDTO(urls, new UrlsDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public UrlsDTO get(final Long id) {
        return urlsRepository.findById(id)
                .map(urls -> urlsMapper.mapToDTO(urls, new UrlsDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UrlsDTO urlsDTO) {
        final Urls urls = new Urls();
        urlsMapper.mapToEntity(urlsDTO, urls);
        return urlsRepository.save(urls).getId();
    }

    public void update(final Long id, final UrlsDTO urlsDTO) {
        final Urls urls = urlsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        urlsMapper.mapToEntity(urlsDTO, urls);
        urlsRepository.save(urls);
    }

    public void delete(final Long id) {
        final Urls urls = urlsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        urlsRepository.delete(urls);
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

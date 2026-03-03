package io.security.base.urls;

import io.security.base.core.BaseEntity;
import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlsMapper {
    private final PrivilegeRepository privilegeRepository;

    public UrlDTO mapToDTO(final Url urls, final UrlDTO urlsDTO) {
        urlsDTO.setId(urls.getId());
        urlsDTO.setEndpoint(urls.getEndpoint());
        urlsDTO.setMethod(urls.getMethod());
        urlsDTO.setPrivileges(urls.getPrivileges().stream().map(BaseEntity::getId).toList());
        return urlsDTO;
    }

    public Url mapToEntity(final UrlDTO urlsDTO, final Url urls) {
        urls.setEndpoint(urlsDTO.getEndpoint());
        urls.setMethod(urlsDTO.getMethod());
        urlsDTO.getPrivileges().forEach(id -> {
            final Privilege privilege = privilegeRepository.findById(id)
                    .orElseThrow(NotFoundException::new);
            urls.getPrivileges().add(privilege);
        });
        return urls;
    }
}

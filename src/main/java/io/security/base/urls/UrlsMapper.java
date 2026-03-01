package io.security.base.urls;

import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlsMapper {
    private final PrivilegeRepository privilegeRepository;

    public UrlsDTO mapToDTO(final Urls urls, final UrlsDTO urlsDTO) {
        urlsDTO.setId(urls.getId());
        urlsDTO.setEndpoint(urls.getEndpoint());
        urlsDTO.setMethod(urls.getMethod());
        urlsDTO.setPrivilege(urls.getPrivilege() == null ? null : urls.getPrivilege().getId());
        return urlsDTO;
    }

    public Urls mapToEntity(final UrlsDTO urlsDTO, final Urls urls) {
        urls.setEndpoint(urlsDTO.getEndpoint());
        urls.setMethod(urlsDTO.getMethod());
        final Privilege privilege = urlsDTO.getPrivilege() == null ? null : privilegeRepository.findById(urlsDTO.getPrivilege())
                .orElseThrow(() -> new NotFoundException("privilege not found"));
        urls.setPrivilege(privilege);
        return urls;
    }
}

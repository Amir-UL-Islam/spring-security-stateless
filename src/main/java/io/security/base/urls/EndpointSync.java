package io.security.base.urls;

import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Transactional
@Order(4)
public class EndpointSync implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(EndpointSync.class);
    private final RequestMappingHandlerMapping handlerMapping;
    private final UrlsRepository urlsRepository;
    private final PrivilegeRepository privilegeRepository;

    public EndpointSync(final RequestMappingHandlerMapping handlerMapping,
                        final UrlsRepository urlsRepository,
                        final PrivilegeRepository privilegeRepository) {
        this.handlerMapping = handlerMapping;
        this.urlsRepository = urlsRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final Privilege adminPrivilege = privilegeRepository.findByNameIgnoreCase("ADMIN")
                .orElse(null);
        if (adminPrivilege == null) {
            log.warn("ADMIN privilege missing; skipping endpoint sync");
            return;
        }

        for (RequestMappingInfo info : handlerMapping.getHandlerMethods().keySet()) {
            final Set<String> patterns = info.getPatternValues();
            final Set<String> methods = info.getMethodsCondition().getMethods().isEmpty()
                    ? Set.of("GET")
                    : info.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.toSet());
            for (String pattern : patterns) {
                if (pattern.startsWith("/error") || pattern.startsWith("/swagger") || pattern.startsWith("/v3")) {
                    continue;
                }
                for (String method : methods) {
                    if (urlsRepository.existsByEndpointIgnoreCaseAndMethodIgnoreCase(pattern, method)) {
                        continue;
                    }
                    final Urls urls = new Urls();
                    urls.setEndpoint(pattern);
                    urls.setMethod(method);
                    urls.setPrivilege(adminPrivilege);
                    urlsRepository.save(urls);
                    log.info("Synced endpoint {} {}", method, pattern);
                }
            }
        }
    }
}

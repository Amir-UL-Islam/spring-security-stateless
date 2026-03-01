package io.security;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;


public class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(BaseSecurityApplication.class);

    @Test
    void verifyModuleStructure() {
        modules.verify();
    }

    /**
     * Generates documentation in build/spring-modulith-docs.
     */
    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

}

package io.security.base.urls;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UrlDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @UrlsEndpointUnique
    private String endpoint;

    @NotNull
    @Size(max = 255)
    private String method;

    @NotNull
    private List<Long> privileges;

}

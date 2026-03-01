package io.security.base.urls;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UrlsDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @UrlsEndpointUnique
    private String endpoint;

    @NotNull
    @Size(max = 255)
    private String method;

    @NotNull
    private Long privilege;

}

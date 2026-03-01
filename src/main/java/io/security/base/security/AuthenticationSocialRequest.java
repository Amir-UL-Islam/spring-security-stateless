package io.security.base.security;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthenticationSocialRequest {

    @NotNull
    private String code;

}

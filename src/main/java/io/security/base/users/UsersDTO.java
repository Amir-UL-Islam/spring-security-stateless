package io.security.base.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsersDTO {

    private Long id;

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    @UsersUsernameUnique
    private String username;

    @NotNull
    @Size(max = 255)
    private String password;

    private List<Long> role;

    private Boolean twoFactorEnabled;

    private String totpSecret;

}

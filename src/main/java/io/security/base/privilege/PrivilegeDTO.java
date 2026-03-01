package io.security.base.privilege;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PrivilegeDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @PrivilegeNameUnique
    private String name;

}

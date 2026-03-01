package io.security.base.role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RoleDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @RoleNameUnique
    private String name;

    @NotNull
    @Size(max = 255)
    @RoleDescriptionUnique
    private String description;

    private List<Long> privilege;

}

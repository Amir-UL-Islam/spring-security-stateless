package io.security.base.privilege;

import io.security.base.security.UserRoles;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/privileges", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2-password")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    public PrivilegeController(final PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @GetMapping
    public ResponseEntity<List<PrivilegeDTO>> getAllPrivileges() {
        return ResponseEntity.ok(privilegeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeDTO> getPrivilege(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(privilegeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPrivilege(
            @RequestBody @Valid final PrivilegeDTO privilegeDTO) {
        final Long createdId = privilegeService.create(privilegeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePrivilege(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PrivilegeDTO privilegeDTO) {
        privilegeService.update(id, privilegeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePrivilege(@PathVariable(name = "id") final Long id) {
        privilegeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

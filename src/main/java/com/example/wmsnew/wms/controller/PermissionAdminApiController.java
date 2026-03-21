package com.example.wmsnew.wms.controller;

import com.example.wmsnew.wms.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/admin")
public class PermissionAdminApiController {
    private final AuthService authService;

    public PermissionAdminApiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/roles")
    public List<AuthService.RoleRow> listRoles() {
        return authService.listRoles();
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRole(@RequestBody @Valid CreateRoleReq req) {
        authService.createRole(req.code(), req.name(), req.permissionIds());
    }

    @PutMapping("/roles/{roleId}")
    public void updateRole(@PathVariable long roleId, @RequestBody @Valid UpdateRoleReq req) {
        authService.updateRole(roleId, req.name(), req.permissionIds());
    }

    @DeleteMapping("/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable long roleId) {
        authService.deleteRole(roleId);
    }

    @GetMapping("/roles-with-directories")
    public List<AuthService.RoleWithDirectoriesRow> listRolesWithDirectories() {
        return authService.listRolesWithDirectories();
    }

    @PutMapping("/roles/{roleId}/permissions")
    public void updateRolePermissions(@PathVariable long roleId, @RequestBody UpdateRolePermissionsReq req) {
        authService.updateRolePermissions(roleId, req.permissionIds());
    }

    @GetMapping("/directories")
    public List<AuthService.DirectoryRow> listDirectories() {
        return authService.listDirectories();
    }

    @PostMapping("/directories")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDirectory(@RequestBody @Valid CreateDirectoryReq req) {
        authService.createDirectory(req.dirKey(), req.dirName(), req.sortOrder() != null ? req.sortOrder() : 0, req.roleIds());
    }

    @PutMapping("/directories/{dirId}")
    public void updateDirectory(@PathVariable long dirId, @RequestBody @Valid UpdateDirectoryReq req) {
        authService.updateDirectory(dirId, req.dirName(), req.sortOrder(), req.roleIds());
    }

    @DeleteMapping("/directories/{dirId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirectory(@PathVariable long dirId) {
        authService.deleteDirectory(dirId);
    }

    public record CreateDirectoryReq(@NotBlank String dirKey, String dirName, Integer sortOrder, List<Long> roleIds) {}
    public record UpdateDirectoryReq(String dirName, Integer sortOrder, List<Long> roleIds) {}
    public record CreateRoleReq(@NotBlank String code, String name, List<Long> permissionIds) {}
    public record UpdateRoleReq(String name, List<Long> permissionIds) {}
    public record UpdateRolePermissionsReq(List<Long> permissionIds) {}
}

package com.example.wmsnew.wms.controller;

import com.example.wmsnew.wms.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/admin/accounts")
public class AccountAdminApiController {
    private final AuthService authService;

    public AccountAdminApiController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public List<AuthService.AccountRow> list() {
        return authService.listAccounts();
    }

    @GetMapping("/{userId}")
    public AuthService.AccountRow get(@PathVariable long userId) {
        return authService.getAccount(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid CreateAccountReq req) {
        authService.createUser(req.username(), req.password(), req.displayName(), req.status(), req.roleIds());
    }

    @PutMapping("/{userId}")
    public void update(@PathVariable long userId, @RequestBody @Valid UpdateAccountReq req) {
        authService.updateUser(userId, req.displayName(), req.status(), req.roleIds());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        authService.deleteUser(userId);
    }

    @PutMapping("/{userId}/password")
    public void resetPassword(@PathVariable long userId, @RequestBody @Valid ResetPasswordReq req) {
        authService.resetPassword(userId, req.newPassword());
    }

    public record ResetPasswordReq(@NotBlank String newPassword) {}
    public record CreateAccountReq(@NotBlank String username, @NotBlank String password, String displayName, String status, List<Long> roleIds) {}
    public record UpdateAccountReq(String displayName, String status, List<Long> roleIds) {}
}

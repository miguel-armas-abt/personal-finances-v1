package io.github.miguelarmasabt.personal.finances.auth.signin.dto;

public record GoogleIdentity(
    String googleSub,
    String email,
    String fullName,
    String pictureUrl
) {
}
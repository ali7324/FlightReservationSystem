package com.example.flightreservationsystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    /* ---- OK ---- */
    public static <U> ApiResponse<U> ok(U data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <U> ApiResponse<U> ok(String message, U data) {
        return new ApiResponse<>(true, message, data);
    }

    /* ---- FAIL ---- */
    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <U> ApiResponse<U> fail(String message, U data) {
        return new ApiResponse<>(false, message, data);
    }

    /* ---- (İstəsən əvvəlki adlar da qalsın) ---- */
    public static <U> ApiResponse<U> success(U data) { return ok(data); }
    public static <U> ApiResponse<U> success(String message, U data) { return ok(message, data); }
    public static ApiResponse<Void> error(String message) { return fail(message); }
    public static <U> ApiResponse<U> error(String message, U data) { return fail(message, data); }
}

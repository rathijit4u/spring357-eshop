package com.mourathi.dto;

import com.mourathi.entity.CartStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CartUpdateRequest {
    @NotBlank
    private CartStatus status;

    public CartUpdateRequest() {
    }

    public CartStatus getStatus() {
        return status;
    }

}

package com.mourathi.dto;

import java.util.List;

public record UserRoleRequest(List<String> roles, OperationType operationType) {
}

package com.beenie.backend.web.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "태그 이름은 필수입니다.")
        @Size(min = 1, max = 50, message = "태그 이름은 1~50자여야 합니다.")
        String name
) {
}

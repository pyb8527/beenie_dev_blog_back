package com.beenie.backend.support.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugGeneratorTest {

    @Test
    void generatesLowercaseAsciiSlugFromEnglishTitle() {
        String slug = SlugGenerator.generate("Hello World Spring Boot");
        assertThat(slug).isEqualTo("hello-world-spring-boot");
    }

    @Test
    void romanizesKoreanTitleIntoAsciiSlug() {
        String slug = SlugGenerator.generate("안녕하세요 블로그");

        assertThat(slug).isNotBlank();
        assertThat(slug).matches("[a-z0-9-]+");
        assertThat(slug).doesNotContain(" ");
    }

    @Test
    void stripsSpecialCharactersAndCollapsesDashes() {
        String slug = SlugGenerator.generate("Java & Spring!! ---  Boot??");
        assertThat(slug).isEqualTo("java-spring-boot");
    }

    @Test
    void fallsBackToDefaultWhenTitleIsBlank() {
        assertThat(SlugGenerator.generate("")).isEqualTo("post");
        assertThat(SlugGenerator.generate(null)).isEqualTo("post");
    }

    @Test
    void appendsNumericSuffixForDuplicateHandling() {
        assertThat(SlugGenerator.withSuffix("my-post", 1)).isEqualTo("my-post-1");
        assertThat(SlugGenerator.withSuffix("my-post", 2)).isEqualTo("my-post-2");
    }
}

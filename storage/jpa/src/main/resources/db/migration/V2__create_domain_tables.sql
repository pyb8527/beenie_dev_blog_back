-- Beenie Dev Blog core schema (MySQL 8)

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    github_id   VARCHAR(100) NOT NULL,
    username    VARCHAR(100) NOT NULL,
    avatar_url  VARCHAR(500) NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_users_github_id UNIQUE (github_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(110) NOT NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE tags (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_tags_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE posts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200)  NOT NULL,
    slug            VARCHAR(220)  NOT NULL,
    content         MEDIUMTEXT    NOT NULL,
    html_content    MEDIUMTEXT    NOT NULL,
    summary         VARCHAR(300)  NULL,
    thumbnail_url   VARCHAR(500)  NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    view_count      BIGINT        NOT NULL DEFAULT 0,
    like_count      BIGINT        NOT NULL DEFAULT 0,
    bookmark_count  BIGINT        NOT NULL DEFAULT 0,
    category_id     BIGINT        NULL,
    author_id       BIGINT        NOT NULL,
    deleted_at      DATETIME(6)   NULL,
    created_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_posts_slug UNIQUE (slug),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_posts_status_created ON posts (status, created_at DESC);
CREATE INDEX idx_posts_category ON posts (category_id);
CREATE INDEX idx_posts_deleted_at ON posts (deleted_at);
-- ngram 파서: 한국어 등 CJK 언어의 형태소 분리가 어려운 문제를 완화하기 위해 2-gram 단위로 색인한다.
CREATE FULLTEXT INDEX ft_posts_title_content ON posts (title, content) WITH PARSER ngram;

CREATE TABLE post_tags (
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT        NOT NULL,
    author_id   BIGINT        NOT NULL,
    parent_id   BIGINT        NULL,
    content     VARCHAR(500)  NOT NULL,
    is_deleted  TINYINT(1)    NOT NULL DEFAULT 0,
    deleted_at  DATETIME(6)   NULL,
    created_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_comments_post ON comments (post_id);
CREATE INDEX idx_comments_parent ON comments (parent_id);

CREATE TABLE post_likes (
    user_id     BIGINT      NOT NULL,
    post_id     BIGINT      NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE post_bookmarks (
    user_id     BIGINT      NOT NULL,
    post_id     BIGINT      NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_post_bookmarks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE site_settings (
    id                  BIGINT PRIMARY KEY,
    blog_title          VARCHAR(200)  NOT NULL DEFAULT 'Beenie Dev Blog',
    blog_description    VARCHAR(500)  NULL,
    keywords            VARCHAR(500)  NULL,
    robots_txt          TEXT          NULL,
    ga_id               VARCHAR(50)   NULL,
    gsc_verification    VARCHAR(200)  NULL,
    updated_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

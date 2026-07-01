-- 기본 '미분류' 카테고리 (id=1 고정, 삭제 불가 취급)
INSERT INTO categories (id, name, slug) VALUES (1, '미분류', 'uncategorized');
ALTER TABLE categories AUTO_INCREMENT = 2;

-- 기본 SEO 설정 단일 행 (id=1 고정)
INSERT INTO site_settings (id, blog_title, blog_description, keywords, robots_txt, ga_id, gsc_verification)
VALUES (1, 'Beenie Dev Blog', '개인 기술 블로그 & 포트폴리오', 'blog,spring,java', 'User-agent: *\nAllow: /', NULL, NULL);

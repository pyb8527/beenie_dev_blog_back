-- ADMIN-006 화면(일반 설정 탭)에서 필요한 필드 보강: 부제, 프로필 이미지 URL, SEO 메타 설명(소개글과 별도)
ALTER TABLE site_settings
    ADD COLUMN blog_subtitle     VARCHAR(200) NULL AFTER blog_title,
    ADD COLUMN profile_image_url VARCHAR(500) NULL AFTER blog_description,
    ADD COLUMN meta_description  VARCHAR(300) NULL AFTER profile_image_url;

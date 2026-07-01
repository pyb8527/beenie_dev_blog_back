package com.beenie.backend.infrastructure.security.oauth2;

import com.beenie.backend.domain.user.User;
import com.beenie.backend.domain.user.UserRepository;
import com.beenie.backend.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GitHub OAuth2 로그인 시 사용자 정보를 조회하고, User 를 upsert 한다.
 * 허용된 GitHub 계정(ALLOWED_GITHUB_ID)만 ROLE_ADMIN, 그 외는 ROLE_USER.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    public static final String ATTR_INTERNAL_USER_ID = "internal_user_id";
    public static final String ATTR_INTERNAL_ROLE = "internal_role";

    private final UserRepository userRepository;

    @Value("${app.allowed-github-id:}")
    private String allowedGithubId;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String githubId = String.valueOf(attributes.get("id"));
        String username = attributes.get("login") != null
                ? String.valueOf(attributes.get("login"))
                : String.valueOf(attributes.getOrDefault("name", "unknown"));
        String avatarUrl = (String) attributes.get("avatar_url");

        UserRole role = (!allowedGithubId.isBlank() && allowedGithubId.equals(githubId))
                ? UserRole.ADMIN
                : UserRole.USER;

        User user = userRepository.findByGithubId(githubId)
                .map(existing -> {
                    existing.updateProfile(username, avatarUrl, role);
                    return existing;
                })
                .orElseGet(() -> User.createNew(githubId, username, avatarUrl, role));
        User saved = userRepository.save(user);

        Map<String, Object> enrichedAttributes = new HashMap<>(attributes);
        enrichedAttributes.put(ATTR_INTERNAL_USER_ID, saved.getId());
        enrichedAttributes.put(ATTR_INTERNAL_ROLE, saved.getRole().name());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name())),
                enrichedAttributes,
                "id");
    }
}

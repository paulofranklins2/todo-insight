package org.duckdns.todosummarized.config;

import org.duckdns.todosummarized.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(userDetailsService, jwtAuthenticationFilter);
    }

    @Nested
    @DisplayName("PasswordEncoder Bean")
    class PasswordEncoderTests {

        @Test
        @DisplayName("should return BCryptPasswordEncoder instance")
        void passwordEncoderShouldReturnBCryptInstance() {
            PasswordEncoder encoder = securityConfig.passwordEncoder();

            assertThat(encoder).isNotNull();
            assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        }

        @Test
        @DisplayName("should encode passwords correctly")
        void passwordEncoderShouldEncodePasswordsCorrectly() {
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String rawPassword = "testPassword123";

            String encodedPassword = encoder.encode(rawPassword);

            assertThat(encodedPassword).isNotEqualTo(rawPassword);
            assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
        }

        @Test
        @DisplayName("should reject wrong passwords")
        void passwordEncoderShouldRejectWrongPasswords() {
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String rawPassword = "testPassword123";
            String wrongPassword = "wrongPassword";

            String encodedPassword = encoder.encode(rawPassword);

            assertThat(encoder.matches(wrongPassword, encodedPassword)).isFalse();
        }

        @Test
        @DisplayName("should generate different hashes for same password")
        void passwordEncoderShouldGenerateDifferentHashes() {
            PasswordEncoder encoder = securityConfig.passwordEncoder();
            String password = "samePassword";

            String hash1 = encoder.encode(password);
            String hash2 = encoder.encode(password);

            assertThat(hash1).isNotEqualTo(hash2);
            assertThat(encoder.matches(password, hash1)).isTrue();
            assertThat(encoder.matches(password, hash2)).isTrue();
        }
    }

    @Nested
    @DisplayName("AuthenticationProvider Bean")
    class AuthenticationProviderTests {

        @Test
        @DisplayName("should return DaoAuthenticationProvider instance")
        void authenticationProviderShouldReturnDaoAuthenticationProvider() {
            AuthenticationProvider provider = securityConfig.authenticationProvider();

            assertThat(provider).isNotNull();
            assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);
        }
    }
}


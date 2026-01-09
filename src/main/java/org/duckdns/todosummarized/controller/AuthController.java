package org.duckdns.todosummarized.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.config.JwtProperties;
import org.duckdns.todosummarized.dto.AuthTokenResponseDTO;
import org.duckdns.todosummarized.dto.RefreshTokenRequestDTO;
import org.duckdns.todosummarized.dto.UserLoginDTO;
import org.duckdns.todosummarized.dto.UserRegistrationDTO;
import org.duckdns.todosummarized.dto.UserResponseDTO;
import org.duckdns.todosummarized.exception.ErrorResponse;
import org.duckdns.todosummarized.exception.InvalidJwtTokenException;
import org.duckdns.todosummarized.service.JwtService;
import org.duckdns.todosummarized.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user authentication operations with JWT support.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT-based authentication API for sign up, sign in, and token refresh")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Register a new user and return JWT tokens.
     */
    @Operation(summary = "Sign up a new user", description = "Creates a new user account and returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthTokenResponseDTO> signUp(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO createdUser = userService.registerUser(registrationDTO);

        // Load the created user to generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(buildTokenResponse(userDetails));
    }

    /**
     * Authenticate an existing user and return JWT tokens.
     */
    @Operation(summary = "Sign in a user", description = "Authenticates a user and returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthTokenResponseDTO> signIn(@Valid @RequestBody UserLoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        return ResponseEntity.ok(buildTokenResponse(userDetails));
    }

    /**
     * Refresh access token using a valid refresh token.
     */
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new InvalidJwtTokenException("Invalid refresh token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidJwtTokenException("Invalid or expired refresh token");
        }

        // Generate new tokens
        return ResponseEntity.ok(buildTokenResponse(userDetails));
    }

    /**
     * Builds JWT token response for the authenticated user.
     */
    private AuthTokenResponseDTO buildTokenResponse(UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return AuthTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000) // Convert ms to seconds
                .email(userDetails.getUsername())
                .role(role)
                .build();
    }
}

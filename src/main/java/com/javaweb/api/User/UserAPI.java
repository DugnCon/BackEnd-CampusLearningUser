package com.javaweb.api.User;

import java.util.Map;

import com.javaweb.entity.UserEntity;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.GoogleAuthService;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.UserDTO;
import com.javaweb.service.IUserService;
import com.javaweb.service.JwtService;

@RestController
@RequestMapping
public class UserAPI {
	@Autowired
	private IUserService userService;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private GoogleAuthService googleAuthService;
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;

	//user logout
	@PostMapping("/api/auth/logout")
	public ResponseEntity<Object> userLogout() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
		Long userId = myUserDetail.getId();
		return userService.userLogout(userId);
	}
	
	@GetMapping("/api/auth/me")
	public ResponseEntity<Object> getMe() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

		Long userId = myUserDetail.getId();

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found"));

		UserDTO userDTO = modelMapper.map(user, UserDTO.class);

		return ResponseEntity.ok(Map.of("success", true, "data", userDTO));
	}
	
	//user signup
	@PostMapping("/api/auth/register")
	public ResponseEntity<Object> userRegister(@RequestBody UserDTO userDTO) {
		return userService.userRegister(userDTO);
	}
	
	//user login
	@PostMapping("/api/auth/login")
	public ResponseEntity<Object> userLogin(@RequestBody UserDTO userDTO) {

		return userService.userLogin(userDTO);
	}
	
	//Làm mới jwt khi hết hạn
	@PostMapping("/api/auth/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
	    String refreshToken = request.get("refreshToken");

	    if (jwtService.isTokenExpired(refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                             .body(Map.of("error", "Refresh token expired"));
	    }

	    String email = jwtService.extractEmail(refreshToken);

	    // Gen access token mới
	    String newAccessToken = jwtService.generateToken(email);
	    String newRefreshToken = jwtService.generateRefreshToken(email); 

	    return ResponseEntity.ok(Map.of(
	        "token", newAccessToken,
	        "refreshToken", newRefreshToken
	    ));
	}
	
	//Kiểm tra tàu khoản
	@GetMapping("/api/auth/check")
	public ResponseEntity<Object> checkUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
		Long userId = myUserDetail.getId();
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found in check"));
		if(userId != null) {
			return ResponseEntity.ok(Map.of("user", user, "success", true));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.value());
	}
	
	//Check xem tài khoản dùng 2FA không
	@PostMapping("/api/passkeys/check-registration")
	public ResponseEntity<Object> checkRegisFromUser(@RequestBody Map<String,Object> passkey) {
		String email = passkey.get("email").toString();
		return ResponseEntity.ok(Map.of("hasPasskey",true));
	}
	
	//Xác thưc tài khoản google
	@PostMapping("/api/auth/google")
	public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> payload) {
		try {

			String idToken = payload.get("token");
			var googlePayload = googleAuthService.verifyGoogleToken(idToken);

			if (googlePayload == null)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("success", false, "message", "Invalid Google token"));

			String email = googlePayload.getEmail();
			String id = googlePayload.getSubject();
			String name = (String) googlePayload.get("name");
			String picture = (String) googlePayload.get("picture");
			String username = (String) googlePayload.get("given_name");

			Map<String, Object> claims = Map.of(
					"email",email,
					"name", name,
					"picture", picture
			);

			String token = jwtService.generateTokenWithClaims(claims);

			return ResponseEntity.ok(Map.of(
					"success", true,
					"user", userService.userVerifyByGoogle(id,email, name, picture, username),
					"token", token
			));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", e.getMessage()));
		}
	}
	
	//Cái này spring cứ gọi, không biết ném nó đi kiểu gì
	@GetMapping("/api/auth/google")
	public ResponseEntity<?> loginWithGoogleGetData(@RequestParam Map<String, String> payload) {
		/*try {
			String idToken = payload.get("token");
			var googlePayload = googleAuthService.verifyGoogleToken(idToken);

			if (googlePayload == null)
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("success", false, "message", "Invalid Google token"));

			String email = googlePayload.getEmail();
			String name = (String) googlePayload.get("name");
			String picture = (String) googlePayload.get("picture");
			String username = (String) googlePayload.get("given_name");

			Map<String, Object> claims = Map.of(
					"email",email,
					"name", name,
					"picture", picture
			);

			String token = jwtService.generateTokenWithClaims(claims);

			return ResponseEntity.ok(Map.of(
					"success", true,
					//"user", userService.userVerifyByGoogle(email, name, picture, username),
					"token", token
			));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", e.getMessage()));
		}*/
		return null;
	}

	//Khi từ disconect tới conect thì setProviderID = googleId và provider = google, status = off
	@PostMapping("/api/auth/oauth/connect/google")
	public ResponseEntity<Object> connectOAuthProvider(@RequestBody Map<String,Object> payload) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
			Long userId = myUserDetail.getId();
			
			return userService.userConnectGoogle(userId);
		} catch (Exception e) {
			throw  new RuntimeException(e + " Can not login by google appearing some errors");
		}
	}
	
	//Tài khoản google khi disconnect
	@DeleteMapping("/api/auth/oauth/disconnect/google")
	public ResponseEntity<Object> disconnectOAuthProvider() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
			
			Long userId = myUserDetail.getId();
			return userService.userLogout(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	@GetMapping("/api/auth/oauth/connections")
	public ResponseEntity<Object> getOAuthConnections() {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

			Long userId = myUserDetail.getId();
			UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Not found"));

			return ResponseEntity.ok(Map.of("success", true, "connections", user));
		} catch (Exception e) {
			throw new RuntimeException(e + " connections error");
		}
	}

	/*@PostMapping("/passkeys/check-registration")
	public ResponseEntity<Map<String, Boolean>> checkPasskey(@RequestBody Map<String, String> body) {
	    String email = body.get("email");
	    boolean hasPasskey = passkeyService.userHasPasskey(email);
	    return ResponseEntity.ok(Map.of("hasPasskey", hasPasskey));
	}*/

}

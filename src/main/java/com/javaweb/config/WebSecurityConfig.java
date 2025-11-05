package com.javaweb.config;
//import com.javaweb.service.impl.CustomUserDetailService;

import java.util.List;
import java.util.Map;

import com.javaweb.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.javaweb.service.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private CustomUserDetailService customUserDetailService;
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private JwtService jwtService;

    //Trang phân quyền
    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable()//Tắt CRFS token. Nếu để API thì tắt đi cho đỡ rắc rối
                .authorizeRequests()//Bắt đầu cấu hình phân quyền URL
                        //.antMatchers("/admin/building-edit").hasAnyRole("MANAGER")
                        .antMatchers("/admin/**").hasAnyRole("MANAGER","STAFF","ADMIN")//Cấp quyền cho các Role như này
                        .antMatchers("/login", "/resource/**", "/trang-chu", "/api/**").permitAll()//URL này cho phép ai cũng vào được
                .and()
                .formLogin().loginPage("/login")//Bật trang login mà mình custom
                .usernameParameter("username")//Tên field trong form login (trùng với input name trong HTML)
                .passwordParameter("password").permitAll()
                .loginProcessingUrl("/j_spring_security_check")//Khi submit login thì post lên URL này để SprSecu xử lý
                .successHandler(myAuthenticationSuccessHandler())//Nếu success thì chạy ---> CustomSuccessHandler
                .failureUrl("/login?incorrectAccount").and()//Nếu mà login sai thì redirect về URL này
                .logout().logoutUrl("/logout").deleteCookies("JSESSIONID")//Dùng để logout acc và vế sau là xóa cookie
                .and().exceptionHandling().accessDeniedPage("/access-denied").and()//Nếu đăng nhập thành công nhưng không được phân quyền thì bị ghi từ chôi
                .sessionManagement().maximumSessions(1).expiredUrl("/login?sessionTimeout");//Khi session hết hạn
    }*/
	
	/*@Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }*/

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://*.ngrok-free.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .authorizeRequests()
            	//Permitall
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/auth/login", "/api/auth/register", "/api/passkeys/check-registration").permitAll()
                .antMatchers("/api/courses/paypal/success", "/api/courses/paypal/cancel").permitAll()
                .antMatchers("/api/auth/google").permitAll()
                .antMatchers("/api/courses", "/api/courses/{courseId}").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/api/blogs/**", "/api/public/**").permitAll()
                .antMatchers("/uploads/**", "/ws/**").permitAll()
                .antMatchers("/api/events").permitAll()
                .antMatchers("/api/competitions").permitAll()
                .antMatchers("/actuator/**").permitAll()
                
                //Các api test dữ liệu trước khi đưa vào authenticated()
                .antMatchers("/api/courses/enrolled").authenticated()
                .antMatchers("/api/courses/*/progress", "/api/courses/*/payment-history").authenticated()
                .antMatchers("/api/lessons/*/progress").authenticated()
                .antMatchers("/api/courses/*/lessons/*/code-server", "/api/courses/*/lessons/*/submit-code").authenticated()
                .antMatchers("/api/events/*", "/api/events/*/register", "/api/events/*/registration-status", "/api/events/*/cancel-registration").authenticated()
                .antMatchers("/api/posts", "/api/posts/**", "/api/posts/*/like", "/api/posts/*/comments", "/api/posts/comments/*", "/api/posts/*/comments/*").authenticated()
                .antMatchers("/api/enrollments").authenticated()
                .antMatchers("/api/settings/profile-picture").authenticated()
                .antMatchers("/api/user/payment-history", "/api/users/search", "/api/chat/users/search", "/api/chat/conversations").authenticated()
                .antMatchers("/api/friendships/suggestions/random" , "/api/friendships", "/api/friendships/**").authenticated()
                .antMatchers("/api/chat/conversations/*/messages", "/api/chat/messages/*", "/api/chat/conversations/*/files").authenticated()
                .antMatchers("/api/stories/**").authenticated()
                .antMatchers("/api/competitions/*", "/api/competitions/*/register", "/api/competitions/*/start", "/api/competitions/*/problems/*", "/api/competitions/submissions/*", "/api/competitions/*/scoreboard").authenticated()
                
                //Authenticated
                .antMatchers("/api/courses/**/enroll", "/api/courses/**/create-paypal-order", "/api/courses/**/learn").authenticated()
                .antMatchers("/api/payment/paypal/success").authenticated()
                .antMatchers("/api/auth/oauth/**").authenticated()
                .antMatchers("/api/courses/*/check-enrollment").authenticated()
                .antMatchers("/api/user/**", "/api/users/**").authenticated()
                .antMatchers("/api/auth/logout","/api/auth/check", "/api/auth/me").authenticated()
                .antMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "TEACHER")
                .anyRequest().authenticated()
            .and()
                /*.oauth2Login()
                .loginPage("/api/auth/google") // FE trigger login
                //.loginPage("/api/auth/login/test")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization") // Spring default
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .successHandler((request, response, authentication) -> { //Nó sẽ callback về BE để làm token rồi gửi lại lên Fe để check Authenticated
                	
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String email = oAuth2User.getAttribute("email");
                    String name = oAuth2User.getAttribute("name");
                    
                    String token = jwtService.generateTokenWithClaims(Map.of("email", email, "name", name));
                    
                    response.sendRedirect("http://localhost:5004/oauth2/redirect?token=" + token);
                })
                .failureHandler((request, response, exception) -> {
                    response.sendRedirect("http://localhost:5004/login?error");
                })
            .and()*/
            //.logout() => cái này là logout của spring nếu làm thuần java
              //  .logoutUrl("/api/auth/logout")
                //.logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout", "POST"))
                //.deleteCookies("JSESSIONID")
                //.logoutSuccessHandler((request, response, authentication) -> {
                  //  response.setStatus(200);
                //})
            //.and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}

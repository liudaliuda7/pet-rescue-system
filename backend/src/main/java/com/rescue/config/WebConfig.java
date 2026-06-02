package com.rescue.config;

import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired private TokenStore tokenStore;
    @Value("${upload.path}") private String uploadPath;

    private static final Set<String> WHITELIST = Set.of(
        "/auth/login", "/auth/register",
        "/public/", "/uploads/", "/error",
        "/notice/public", "/animal/public", "/help/public",
        "/animal-type/public", "/station/public", "/health/public",
        "/review/public"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File f = new File(uploadPath);
        if (!f.exists()) f.mkdirs();
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + f.getAbsolutePath() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object h) throws Exception {
                String uri = req.getRequestURI();
                String ctx = req.getContextPath();
                if (uri.startsWith(ctx)) uri = uri.substring(ctx.length());
                if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
                for (String w : WHITELIST) if (uri.startsWith(w) || uri.equals(w.replaceAll("/$", ""))) return true;
                String token = req.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
                if (token == null) token = req.getHeader("token");
                User u = tokenStore.get(token);
                if (u == null) {
                    resp.setStatus(401);
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(401, "未登录或登录已过期")));
                    return false;
                }
                TokenStore.setCurrent(u);
                return true;
            }
            @Override
            public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object h, Exception e) {
                TokenStore.clearCurrent();
            }
        }).addPathPatterns("/**");
    }
}

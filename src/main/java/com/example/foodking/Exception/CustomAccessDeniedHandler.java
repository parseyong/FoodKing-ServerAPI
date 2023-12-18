package com.example.foodking.Exception;

import com.example.foodking.Common.CommonResDTO;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(403);
        Gson gson = new Gson();

        CommonResDTO commonResponseDTO = CommonResDTO.of("HttpStatus.FORBIDDEN","권한이 없습니다",null);

        response.getWriter().write(gson.toJson(commonResponseDTO));
    }
}

package sumdu.edu.ua.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.web.dto.ErrorResponse;

import java.io.IOException;

public class JsonResponseWriter {
    private static final Logger log = LoggerFactory.getLogger(JsonResponseWriter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static void writeJson(HttpServletResponse resp, int status, Object payload) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), payload);
    }

    public static void handleException(HttpServletRequest req, HttpServletResponse resp, Exception ex) throws IOException {
        String path = req.getRequestURI();
        if (ex instanceof ValidationException || ex instanceof IllegalArgumentException) {
            log.warn("Client error 400 Bad Request at {}: {}", path, ex.getMessage());
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponse(400, "Bad Request", ex.getMessage(), path));
        } else if (ex instanceof NotFoundException) {
            log.warn("Client error 404 Not Found at {}: {}", path, ex.getMessage());
            writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    new ErrorResponse(404, "Not Found", ex.getMessage(), path));
        } else if (ex instanceof BusinessRuleConflictException) {
            log.warn("Business conflict 409 Conflict at {}: {}", path, ex.getMessage());
            writeJson(resp, HttpServletResponse.SC_CONFLICT,
                    new ErrorResponse(409, "Conflict", ex.getMessage(), path));
        } else {
            log.error("Internal server error 500 at " + path, ex);
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorResponse(500, "Internal Server Error", "Внутрішня помилка сервера", path));
        }
    }
}

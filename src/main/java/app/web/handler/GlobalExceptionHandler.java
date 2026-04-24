package app.web.handler;

import app.web.exception.WebException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, ServletWebRequest request) {
        logger.error(ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequest().getRequestURI()));

        Map<String, Object> error = Map.of(
                "message", ex.getMessage()
        );
        problem.setProperty("errors", error);

        return problem;
    }

    @ExceptionHandler(WebException.class)
    public ProblemDetail handleBadRequestException(WebException ex, ServletWebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());

        problem.setTitle(HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
        problem.setInstance(URI.create(request.getRequest().getRequestURI()));

        Map<String, Object> error = Map.of(
                "message", Objects.requireNonNull(ex.getReason())
        );
        problem.setProperty("error", error);

        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Paramètre invalide");
        problem.setDetail("Problème à la conversion");
        problem.setInstance(URI.create(request.getRequestURI()));

        var expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";

        var value = ex.getValue();
        if(value == null) {
            value = "";
        }

        var exceptionMessage = ex.getMostSpecificCause() instanceof ResponseStatusException ?
                ((ResponseStatusException) ex.getMostSpecificCause()).getReason() :
                ex.getMostSpecificCause().getMessage();
        if(exceptionMessage == null) {
            exceptionMessage = "";
        }

        Map<String, Object> error = Map.of(
                "parameter", Map.of(
                        "name", ex.getName(),
                        "value", value,
                        "expectedType", expectedType
                ),
                "message", exceptionMessage
        );

        problem.setProperty("error", error);

        return problem;
    }

}

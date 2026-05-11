package app.web.handler;

import app.back.exception.*;
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
import java.util.HashMap;
import java.util.Map;

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
        problem.setProperty("error", error);

        return problem;
    }

    @ExceptionHandler(WebException.class)
    public ProblemDetail handleBadRequestException(WebException ex, ServletWebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());

        problem.setTitle(HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
        problem.setInstance(URI.create(request.getRequest().getRequestURI()));

        var message = ex.getReason();
        if(message == null || message.isBlank()) {
            logger.error("Aucun message pour l'exception courante.", ex);
            message = "Une erreur est survenue.";
        }

        Map<String, Object> error = Map.of(
                "message", message
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

        var exceptionMessage = ex.getMostSpecificCause() instanceof ResponseStatusException ?
                ((ResponseStatusException) ex.getMostSpecificCause()).getReason() :
                ex.getMostSpecificCause().getMessage();
        if(exceptionMessage == null) {
            logger.error("Aucun message pour l'exception courante.", ex);
            exceptionMessage = "Impossible de mapper l'élément correctement.";
        }

        Map<String, Object> error = Map.of(
                "parameter", new HashMap<>(){
                    {
                        put("name", ex.getName());
                        put("value", value != null ? value.toString() : null);
                        put("expectedType", expectedType);
                    }
                },
                "message", exceptionMessage
        );

        problem.setProperty("error", error);

        return problem;
    }

    @ExceptionHandler(BackResourceAccessException.class)
    public ProblemDetail handleBackResourceAccessException(BackResourceAccessException ex, ServletWebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequest().getRequestURI()));

        Map<String, Object> error = Map.of(
                "message", "Un problème interne est survenu."
        );
        problem.setProperty("error", error);

        return problem;
    }

    @ExceptionHandler(BackException.class)
    public ProblemDetail handleBadRequestException(BackException ex, ServletWebRequest request) {
        return handleBackException(ex, request);
    }

    private ProblemDetail handleBackException(BackException exception, ServletWebRequest request) {
        HttpStatus status;
        switch(exception) {
            case BackBadRequestException _ -> status = HttpStatus.BAD_REQUEST;
            case BackNotFoundException _ -> status = HttpStatus.NOT_FOUND;
            case BackForbiddenException _ -> status = HttpStatus.FORBIDDEN;
            default -> {
                logger.error("Aucune définition pour la classe d'exception : {}", exception.getClass());
                throw new IllegalStateException("Unexpected value: " + exception);
            }
        }

        ProblemDetail problem = ProblemDetail.forStatus(status);

        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequest().getRequestURI()));

        var message = exception.getMessage();
        if(message == null || message.isBlank()) {
            logger.error("Aucun message pour l'exception courante.", exception);
            message = "Une erreur est survenue.";
        }

        Map<String, Object> error = Map.of(
                "message", message
        );
        problem.setProperty("error", error);

        return problem;
    }

}

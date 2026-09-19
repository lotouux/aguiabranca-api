package com.aguiabranca.api.exception;

import com.aguiabranca.api.dto.CampoErroDTO;
import com.aguiabranca.api.dto.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * A {@code @PreAuthorize} denial is thrown by the AOP interceptor inside {@code DispatcherServlet},
 * so it must be handled here explicitly - without a dedicated handler it falls into the catch-all
 * {@link Exception} handler and every 403 becomes a 500. Filter-chain denials (no authenticated
 * caller reaching a controller at all) never reach this advice; those are handled by
 * {@code RestAuthenticationEntryPoint} / {@code RestAccessDeniedHandler}, which serialize the same
 * {@link ErroResponseDTO} shape.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErroResponseDTO> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDTO> handleNoResourceFound(NoResourceFoundException ex,
            HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", "Recurso não encontrado.", request);
    }

    @ExceptionHandler({ ConflictException.class, DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class })
    public ResponseEntity<ErroResponseDTO> handleConflict(Exception ex, HttpServletRequest request) {
        String mensagem = ex instanceof ConflictException ? ex.getMessage() : "Conflito ao processar a requisição.";
        return build(HttpStatus.CONFLICT, "CONFLITO", mensagem, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean autenticado = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

        if (!autenticado) {
            return build(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO", "Autenticação necessária.", request);
        }
        return build(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", "Você não tem permissão para executar esta ação.",
                request);
    }

    @ExceptionHandler({ AuthenticationException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<ErroResponseDTO> handleAuthentication(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO", "Credenciais inválidas.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<CampoErroDTO> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toCampoErro)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErroResponseDTO.of(Instant.now(clock), HttpStatus.BAD_REQUEST.value(), "REQUISICAO_INVALIDA",
                        "Um ou mais campos são inválidos.", request.getRequestURI(), campos));
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, HttpMessageNotWritableException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ErroResponseDTO> handleMalformedRequest(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA", "A requisição está malformada.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado ao processar {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", "Ocorreu um erro inesperado.", request);
    }

    private CampoErroDTO toCampoErro(FieldError fieldError) {
        return new CampoErroDTO(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private ResponseEntity<ErroResponseDTO> build(HttpStatus status, String erro, String mensagem,
            HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ErroResponseDTO.of(Instant.now(clock), status.value(), erro, mensagem,
                        request.getRequestURI()));
    }
}

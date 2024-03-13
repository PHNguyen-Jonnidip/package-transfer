package com.nguyenphuong.PackageTransfer.config.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({RecordNotFoundException.class, InternalAuthenticationServiceException.class})
  public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
  public ResponseEntity<ExceptionResponse> handleLockedEntityProperties(Exception ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.BAD_REQUEST.toString(), "Can't edit read only fields");
    final String msg = constructLogMessage(ex, request);
    LOGGER.info(msg);
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<ExceptionResponse> handleForbiddenException(Exception ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.FORBIDDEN.toString(), ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<ExceptionResponse> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ExceptionResponse> globalExceptionHandler(Throwable ex, HttpServletRequest request) {
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something went wrong.");
    final String msg = constructLogMessage(ex, request);
    LOGGER.error(msg, ex);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid
      (MethodArgumentNotValidException exception,
       HttpHeaders headers, HttpStatus status, WebRequest request) {
    String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
    ExceptionResponse res = new ExceptionResponse(false, HttpStatus.BAD_REQUEST.toString(), errorMessage);
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  private String constructLogMessage(Throwable exception, HttpServletRequest request) {
    return request.getMethod()
        + " - "
        + request.getRequestURI()
        + " - "
        + exception;
  }
}

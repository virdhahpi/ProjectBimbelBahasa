package com.juaracoding.pcmspringbootcsr.handler;

import com.juaracoding.pcmspringbootcsr.configuration.OtherConfig;
import com.juaracoding.pcmspringbootcsr.util.LoggingFile;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
	KODE EXCEPTION
	VALIDATION		= 01
	DATA			= 02
	AUTH			= 03
	MEDIA / FILE	= 04
	UNKNOW			= 99
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private List<ApiValidationError> lsSubError = new ArrayList<ApiValidationError>();
	private String [] strExceptionArr = new String[2];

	public GlobalExceptionHandler() {
		strExceptionArr[0] = "GlobalExceptionHandler";
	}

	/*
		SAMPLE FORMAT UNTUK EXCEPTION YANG DI OVERRIDE DARI CLASS ResponseEntityExceptionHandler
		DITANDAI DENGAN ANOTASI @Override
	 */
	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers,
																  HttpStatus status,
																  WebRequest request) {
		lsSubError.clear();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			lsSubError.add(new ApiValidationError(fieldError.getField(),fieldError.getDefaultMessage()));
		}
		ApiError apiError =
				new ApiError(HttpStatus.BAD_REQUEST, "Data Tidak Valid !!",ex,request.getDescription(false),"X-01-001");
		apiError.setSubErrors(lsSubError);
		strExceptionArr[1] = "handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers,HttpStatus status,WebRequest request) \n"+RequestCapture.allRequest(request);//perubahan 12-12-2023
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return new ResponseEntity<Object>(apiError,HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return ResponseEntity.status(apiError.getStatus()).body(apiError);
	}

	/*
		SALAH SATU SAMPLE FORMAT UNTUK EXCEPTION YANG DI TIDAK OVERRIDE DARI CLASS ResponseEntityExceptionHandler
		DITANDAI DENGAN ANOTASI @ExceptionHandler
		salah satu kasus nya jika foreign key nya belum di set data nya di table master (parent)
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
		strExceptionArr[1] = "dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"DATA TIDAK VALID",ex,request.getPathInfo(),"X-02-001"));
	}

	/*
		salah satu kasusnya : simulasikan tabel di delete lalu upload file csv untuk produce error exception UnexpectedRollbackException..
	 */
	@ExceptionHandler(UnexpectedRollbackException.class)
	public ResponseEntity<Object> unexpectedRollbackException(UnexpectedRollbackException ex, HttpServletRequest request) {
		strExceptionArr[1] = "unexpectedRollbackException(UnexpectedRollbackException ex, HttpServletRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"FLOW GAGAL DI PROSES",ex,request.getPathInfo(),"X-02-002"));
	}
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<Object> internalAuthenticationServiceException(InternalAuthenticationServiceException ex, HttpServletRequest request) {
		strExceptionArr[1] = "internalAuthenticationServiceException(InternalAuthenticationServiceException ex, HttpServletRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"OTENTIKASI GAGAL",ex,request.getPathInfo(),"X-03-001"));
	}
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> usernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
		strExceptionArr[1] = "UsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED,"USERNAME / PASSWORD SALAH !! ",ex,request.getPathInfo(),"X-03-002"));
	}
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<Object> MultipartException(MultipartException ex, HttpServletRequest request) {
		strExceptionArr[1] = "MultipartException(MultipartException ex, HttpServletRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Konten harus Multipart",ex,request.getPathInfo(),"X-04-001"));
	}
	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<Object> fileUploadException(FileUploadException ex, WebRequest request) {
		strExceptionArr[1] = "fileUploadException(FileUploadException ex, WebRequest request) \n"+RequestCapture.allRequest(request);
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File Tidak Sesuai",ex,request.getDescription(false),"X-04-002"));
	}
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		strExceptionArr[1] = "handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) \n"+RequestCapture.allRequest(request);//perubahan 12-12-2023
		LoggingFile.exceptionStringz(strExceptionArr, ex, OtherConfig.getFlagLoging());
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,"TIDAK DAPAT DIPROSES",ex,request.getDescription(false),"X-99-001"));
	}
}
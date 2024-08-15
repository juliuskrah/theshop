package com.shoperal.core.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import jakarta.servlet.http.HttpServletRequest;

import com.shoperal.core.business.StorageService;
import com.shoperal.core.controller.dto.ShoperalErrorType;
import com.shoperal.core.utility.Profiles;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

/**
 * This controller assists with local testing of file uploads
 * 
 * @author Julius Krah
 */
@Profile(Profiles.LOCAL)
@RestController
public class SimpleFileController {
    private final StorageService storageService;
    
    public SimpleFileController(@Qualifier("fileSystemStorageService") StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(path = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Callable<URI> uploadFile(@RequestParam MultipartFile file, @RequestParam Optional<String> contentType) {
        var mimeType = contentType.orElse(file.getContentType());
        if (mimeType != null)
            MediaType.valueOf(mimeType);
        return () -> storageService.writeFile(file.getBytes(), mimeType);
    }

    @PutMapping(path = "/upload-media")
    public Callable<URI> uploadMedia(@RequestBody byte[] media, @RequestHeader Map<String, String> headers,  @RequestParam Optional<String> contentType) {
        var mimeType = contentType.orElse(headers.get("Content-Type"));
        if (mimeType != null)
            MediaType.valueOf(mimeType);
        return () -> storageService.writeMedia(media, mimeType);
    }

    @GetMapping(path = "/files/media/**")
    public ResponseEntity<byte[]> loadMedia(HttpServletRequest request) throws IOException {
        String uriPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        var contentDisposition = ContentDisposition.builder("inline") //
                .filename(UUID.randomUUID().toString() + ".jpeg").build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        var bytes = storageService.readFile(URI.create(uriPath));
        return ResponseEntity.ok() //
                .header("Content-Type", "image/jpeg") //
                .headers(headers) //
                .body(bytes);
    }

    @GetMapping(path = "/files/uncategorized/**")
    public ResponseEntity<byte[]> loadFile(HttpServletRequest request) throws IOException {
        String uriPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        var contentDisposition = ContentDisposition.builder("attachment") //
                .filename(UUID.randomUUID().toString()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        var bytes = storageService.readFile(URI.create(uriPath));
        return ResponseEntity.ok() //
                .header("Content-Type", "application/octet-stream") //
                .headers(headers) //
                .body(bytes);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    ResponseEntity<?> handleIOException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return new ResponseEntity<>(new ShoperalErrorType(status.value(), ex.getMessage()), status);
    }

    @ExceptionHandler(InvalidMediaTypeException.class)
    @ResponseBody
    ResponseEntity<?> handleInvalidMediaTypeException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ShoperalErrorType(status.value(), ex.getMessage()), status);
    }

}

package com.shoperal.core.business;

import static com.shoperal.core.utility.AWSS3Utilities.SHA256Hash;
import static com.shoperal.core.utility.AWSS3Utilities.authorization;
import static com.shoperal.core.utility.AWSS3Utilities.canonicalRequest;
import static com.shoperal.core.utility.AWSS3Utilities.credential;
import static com.shoperal.core.utility.AWSS3Utilities.hex;
import static com.shoperal.core.utility.AWSS3Utilities.mapToHeaders;
import static com.shoperal.core.utility.AWSS3Utilities.mapToQueryString;
import static com.shoperal.core.utility.AWSS3Utilities.mapToSignedHeaders;
import static com.shoperal.core.utility.AWSS3Utilities.signature;
import static com.shoperal.core.utility.AWSS3Utilities.stringToSign;
import static com.shoperal.core.utility.AWSS3Utilities.uriEncode;

import java.io.IOException;
import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.shoperal.core.ApplicationProperties;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of StorageService that saves files to S3 bucket. This is a
 * cloud drive implementation
 * 
 * @author Julius Krah
 */
@Primary
@Component
@RequiredArgsConstructor
public class AWSS3StorageService implements StorageService {
    private final ApplicationProperties properties;
    private final RestTemplate restTemplate;

    private String getAuthorizationHeader(String httpMethod, String uri, ZonedDateTime date, //
            Map<String, String> additionalHeaders, Map<String, String> canonicalQueryString, byte[] payload) {
        var s3 = properties.getS3();
        var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        var ISODate = date.format(dateFormatter);
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
        var ISOtimeStamp = date.format(formatter);

        var canonicalHeaders = new HashMap<String, String>();
        canonicalHeaders.put("Host", s3.getAwsHost());
        if (additionalHeaders != null) {
            canonicalHeaders.putAll(additionalHeaders);
        }
        var signedHeaders = mapToSignedHeaders(canonicalHeaders);

        var canonicalRequest = canonicalRequest(httpMethod, uri, //
                canonicalQueryString, canonicalHeaders, payload);
        var stringToSign = stringToSign(ISODate, ISOtimeStamp, s3.getAwsRegion(), s3.getAwsService(), canonicalRequest);
        var signature = signature(s3.getAwsSecretAccessKey(), ISODate, s3.getAwsRegion(), s3.getAwsService(),
                stringToSign);
        var authorization = authorization(s3.getAwsAccessKeyId(), ISODate, s3.getAwsRegion(), s3.getAwsService(),
                signedHeaders, signature);
        return authorization;
    }

    @Override
    public URI writeMedia(byte[] file, String contentType) throws IOException {
        var s3 = properties.getS3();
        var date = ZonedDateTime.now(ZoneOffset.UTC);
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
        var dateString = date.format(formatter);
        var payloadHash = hex(SHA256Hash(file));
        var headers = new HttpHeaders();
        headers.add("X-Amz-Content-Sha256", payloadHash);
        headers.add("X-Amz-Date", dateString);
        headers.add("X-Amz-Acl", "bucket-owner-full-control");
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        var canonicalUri = "/" + properties.getTenant().getName() + "/media/" + UUID.randomUUID() + "/imagefile";
        var uri = UriComponentsBuilder.newInstance() //
                .host(s3.getAwsHost()).path(canonicalUri).scheme("https").build().toUri();

        Map<String, String> additionalHeaders = Map.of( //
                "X-Amz-Content-Sha256", payloadHash, //
                "X-Amz-Date", dateString, //
                "X-Amz-Acl", "bucket-owner-full-control", //
                HttpHeaders.CONTENT_TYPE, contentType);
        var authorization = getAuthorizationHeader("PUT", canonicalUri, date, additionalHeaders, null, file);
        headers.add(HttpHeaders.AUTHORIZATION, authorization);

        RequestEntity<byte[]> entity = new RequestEntity<>(file, headers, HttpMethod.PUT, uri);
        restTemplate.exchange(entity, Void.class);
        return uri;
    }

    @Override
    public URI writeImageThumbnail(byte[] file, String contentType) throws IOException {
        throw new UnsupportedOperationException("S3 Operations use signed URI");
    }

    @Override
    public URI writeFile(byte[] file, String contentType) throws IOException {
        throw new UnsupportedOperationException("S3 Operations use signed URI");
    }

    @Override
    public byte[] readFile(URI uri) throws IOException {
        throw new UnsupportedOperationException("Read directly from the S3 bucket");
    }

    /**
     * {@inheritDoc}
     * 
     * @see https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-query-string-auth.html
     */
    @Override
    public URI getPreSignedURI(String filename, String contentType) {
        var s3 = properties.getS3();

        var date = ZonedDateTime.now(ZoneOffset.UTC);
        var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
        var ISODate = date.format(dateFormatter);
        var ISOTimeStamp = date.format(formatter);

        Map<String, String> headers = Map.of( //
                "Host", s3.getAwsHost(), //
                "X-Amz-Acl", "bucket-owner-full-control", //
                HttpHeaders.CONTENT_TYPE, contentType);

        var canonicalUri = "/" + properties.getTenant().getName() + "/media/" + UUID.randomUUID() + "/" + filename;
        var credential = credential(s3.getAwsAccessKeyId(), ISODate, s3.getAwsRegion(), s3.getAwsService());
        var expires = s3.getPresignedURIValidity().getSeconds();
        var canonicalSignedHeaders = mapToSignedHeaders(headers);

        Map<String, String> queryString = Map.of( //
                "X-Amz-Algorithm", "AWS4-HMAC-SHA256", //
                "X-Amz-Credential", credential, //
                "X-Amz-Date", ISOTimeStamp, //
                "X-Amz-Expires", "" + expires, //
                "X-Amz-SignedHeaders", canonicalSignedHeaders);
        var canonicalQueryString = mapToQueryString(queryString);
        var canonicalHeaders = mapToHeaders(headers);
        var canonicalRequest = canonicalRequest("PUT", canonicalUri, canonicalQueryString, canonicalHeaders,
                canonicalSignedHeaders, null);
        var stringToSign = stringToSign(ISODate, ISOTimeStamp, s3.getAwsRegion(), s3.getAwsService(), canonicalRequest);
        var signature = signature(s3.getAwsSecretAccessKey(), ISODate, s3.getAwsRegion(), s3.getAwsService(),
                stringToSign);

        var uri = UriComponentsBuilder.newInstance() //
                .host(s3.getAwsHost()).path(canonicalUri).scheme("https") //
                .queryParam("X-Amz-Algorithm", "AWS4-HMAC-SHA256") //
                .queryParam("X-Amz-Credential", uriEncode(credential, true)) //
                .queryParam("X-Amz-Date", ISOTimeStamp) //
                .queryParam("X-Amz-Expires", expires) //
                .queryParam("X-Amz-SignedHeaders", canonicalSignedHeaders) //
                .queryParam("X-Amz-Signature", signature) //
                .build(true).toUri();
        return uri;
    }

}

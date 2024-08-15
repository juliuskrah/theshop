package com.shoperal.core.utility;

import static com.shoperal.core.utility.AWSS3Utilities.SHA256Hash;
import static com.shoperal.core.utility.AWSS3Utilities.hex;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Julius Krah
 */
public class AWSS3UtilitiesTest {
        private final static String AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE";
        private final static String AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

        @Test
        void testCanonicalRequest() {
                var httpMethod = "PUT";
                var date = ZonedDateTime.of(2021, 1, 05, 1, 52, 45, 0, ZoneOffset.UTC);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var dateString = date.format(formatter);
                var rand = UUID.randomUUID();
                var canonicalURI = "/main/" + rand + "/a file@version 1.png";
                var canonicalQueryString = Map.<String, String>of("prefix", "main/" + rand, "max-keys", "2");
                var canonicalHeaders = Map.of("Host", " shoperaldev.s3.eu-west-1.amazonaws.com", //
                                "Content-Type", "image/png ", //
                                "x-amz-date", dateString);
                var payload = "".getBytes(UTF_8);

                String canonicalRequest = AWSS3Utilities.canonicalRequest(httpMethod, canonicalURI,
                                canonicalQueryString, canonicalHeaders, payload);
                var expected = "PUT\n" //
                                + "/main/" + rand + "/a%20file%40version%201.png\n" //
                                + "max-keys=2&prefix=main%2F" + rand + "\n" //
                                + "content-type:image/png\n" //
                                + "host:shoperaldev.s3.eu-west-1.amazonaws.com\n" //
                                + "x-amz-date:20210105T015245Z\n" //
                                + "\n" //
                                + "content-type;host;x-amz-date\n"
                                + "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
                assertThat(canonicalRequest).isEqualTo(expected);
        }

        @Test
        @DisplayName("Test when no query string is provided")
        void testCanonicalRequestWhenQuery() {
                var httpMethod = "PUT";
                var rand = UUID.randomUUID();
                var date = ZonedDateTime.of(2013, 7, 8, 22, 8, 55, 0, ZoneOffset.UTC);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var dateString = date.format(formatter);
                var canonicalURI = "/main/" + rand + "/a file.png";
                var canonicalHeaders = Map.of("Host", "shoperaldev.s3.eu-west-1.amazonaws.com", //
                                "Content-Type", "image/png", //
                                "x-amz-date", dateString);
                var payload = "".getBytes(UTF_8);

                String canonicalRequest = AWSS3Utilities.canonicalRequest(httpMethod, canonicalURI, null,
                                canonicalHeaders, payload);

                var expected = "PUT\n" //
                                + "/main/" + rand + "/a%20file.png\n" //
                                + "\n" //
                                + "content-type:image/png\n" //
                                + "host:shoperaldev.s3.eu-west-1.amazonaws.com\n" //
                                + "x-amz-date:20130708T220855Z\n" //
                                + "\n" //
                                + "content-type;host;x-amz-date\n"
                                + "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
                assertThat(canonicalRequest).isEqualTo(expected);
        }

        @Test
        void testCalculateEmptyHash() {
                byte[] emptyString = "".getBytes(UTF_8);
                var hash = hex(SHA256Hash(emptyString));
                assertThat(hash).isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        }

        @Test
        @DisplayName("ASW S3 test suite 1 for CanonicalRequest")
        void canonicalRequestTestSuiteOne() {
                var date = ZonedDateTime.of(2013, 5, 24, 0, 0, 0, 0, ZoneOffset.UTC);
                String canonicalRequest = canonicalRequest(date);
                var expected = "GET\n" //
                                + "/test.txt\n" //
                                + "\n" //
                                + "host:examplebucket.s3.amazonaws.com\n" //
                                + "range:bytes=0-9\n" //
                                + "x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" //
                                + "x-amz-date:20130524T000000Z\n" //
                                + "\n" //
                                + "host;range;x-amz-content-sha256;x-amz-date\n" //
                                + "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
                assertThat(canonicalRequest).isEqualTo(expected);
        }

        @Test
        @DisplayName("ASW S3 test suite 2 for CanonicalRequest")
        void canonicalRequestTestSuiteTwo() {
                var service = "s3";
                var region = "us-east-1";
                var date = ZonedDateTime.of(2013, 5, 24, 0, 0, 0, 0, ZoneOffset.UTC);
                var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                var ISODate = date.format(dateFormatter);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var dateString = date.format(formatter);
                var canonicalHeaders = Map.of("Host", "examplebucket.s3.amazonaws.com");
                var signedHeaders = AWSS3Utilities.mapToSignedHeaders(canonicalHeaders);
                var headers = AWSS3Utilities.mapToHeaders(canonicalHeaders);
                var canonicalURI = "/test.txt";
                var canonicalQueryString = Map.of("X-Amz-Expires", "86400", "X-Amz-SignedHeaders", signedHeaders,
                                "X-Amz-Date", dateString, "X-Amz-Algorithm", "AWS4-HMAC-SHA256", "X-Amz-Credential",
                                AWSS3Utilities.credential(AWS_ACCESS_KEY_ID, ISODate, region, service));
                var queryString = AWSS3Utilities.mapToQueryString(canonicalQueryString);

                String canonicalRequest = AWSS3Utilities.canonicalRequest("GET", canonicalURI, queryString, headers,
                                signedHeaders, null);
                var expected = "GET\n" //
                                + "/test.txt\n" //
                                + "X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20130524T000000Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host\n" //
                                + "host:examplebucket.s3.amazonaws.com\n" //
                                + "\n" //
                                + "host\n" //
                                + "UNSIGNED-PAYLOAD";
                assertThat(canonicalRequest).isEqualTo(expected);
        }

        @Test
        @DisplayName("ASW S3 test suite 1 for StringToSign")
        void stringToSignTestSuiteOne() {
                var date = ZonedDateTime.of(2013, 5, 24, 0, 0, 0, 0, ZoneOffset.UTC);
                var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                var ISODate = date.format(dateFormatter);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var ISOtimeStamp = date.format(formatter);
                String canonicalRequest = canonicalRequest(date);
                String stringToSign = AWSS3Utilities.stringToSign(ISODate, ISOtimeStamp, "us-east-1", "s3",
                                canonicalRequest);
                var expected = "AWS4-HMAC-SHA256\n" //
                                + "20130524T000000Z\n" //
                                + "20130524/us-east-1/s3/aws4_request\n" //
                                + "7344ae5b7ee6c3e7e6b0fe0640412a37625d1fbfff95c48bbb2dc43964946972";
                assertThat(stringToSign).isEqualTo(expected);
        }

        @Test
        @DisplayName("ASW S3 test suite 1 for signature")
        void signatureTestSuiteOne() {
                var service = "s3";
                var region = "us-east-1";
                var date = ZonedDateTime.of(2013, 5, 24, 0, 0, 0, 0, ZoneOffset.UTC);
                var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                var ISODate = date.format(dateFormatter);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var ISOtimeStamp = date.format(formatter);
                String canonicalRequest = canonicalRequest(date);
                String stringToSign = AWSS3Utilities.stringToSign(ISODate, ISOtimeStamp, region, service,
                                canonicalRequest);
                String signature = AWSS3Utilities.signature(AWS_SECRET_ACCESS_KEY, ISODate, region, service,
                                stringToSign);
                assertThat(signature).isEqualTo("f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41");
        }

        @Test
        @DisplayName("ASW S3 test suite 1 for signature")
        void authorizationHeaderTestSuiteOne() {
                var signature = signature();
                var expected = "AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/us-east-1/s3/aws4_request,SignedHeaders=host;range;x-amz-content-sha256;x-amz-date,Signature=f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41";
                assertThat(signature).isEqualTo(expected);
        }

        private String canonicalRequest(ZonedDateTime date) {
                var httpMethod = "GET";
                var payload = "".getBytes(UTF_8);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var dateString = date.format(formatter);
                var canonicalURI = "/test.txt";
                var canonicalHeaders = Map.of("Host", "examplebucket.s3.amazonaws.com", //
                                "Range", "bytes=0-9", //
                                "x-amz-content-sha256", hex(SHA256Hash(payload)), //
                                "x-amz-date", dateString);

                return AWSS3Utilities.canonicalRequest(httpMethod, canonicalURI, null, canonicalHeaders, payload);
        }

        private String signature() {
                var service = "s3";
                var region = "us-east-1";
                var httpMethod = "GET";
                var payload = "".getBytes(UTF_8);
                var date = ZonedDateTime.of(2013, 5, 24, 0, 0, 0, 0, ZoneOffset.UTC);
                var formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssz");
                var dateString = date.format(formatter);
                var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                var ISODate = date.format(dateFormatter);
                var canonicalURI = "/test.txt";
                var canonicalHeaders = Map.of("Host", "examplebucket.s3.amazonaws.com", //
                                "Range", "bytes=0-9", //
                                "x-amz-content-sha256", hex(SHA256Hash(payload)), //
                                "x-amz-date", dateString);
                var signedHeaders = AWSS3Utilities.mapToSignedHeaders(canonicalHeaders);

                String canonicalRequest = AWSS3Utilities.canonicalRequest(httpMethod, canonicalURI, null,
                                canonicalHeaders, payload);
                String stringToSign = AWSS3Utilities.stringToSign(ISODate, dateString, region, service,
                                canonicalRequest);
                String signature = AWSS3Utilities.signature(AWS_SECRET_ACCESS_KEY, ISODate, region, service,
                                stringToSign);
                return AWSS3Utilities.authorization(AWS_ACCESS_KEY_ID, ISODate, region, service, signedHeaders,
                                signature);
        }
}

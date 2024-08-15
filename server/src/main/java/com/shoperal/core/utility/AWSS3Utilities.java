package com.shoperal.core.utility;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.lang.Nullable;

/**
 * Utility classes for working with AWS S3
 * 
 * @author Julius Krah
 */
public class AWSS3Utilities {
    private AWSS3Utilities() {
    }

    private static String toHexUTF8(char ch) {
        return new StringBuilder().append('%') //
                .append(toHex(ch / 16)) //
                .append(toHex(ch % 16)).toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    /**
     * Transforms a map to a uri encoded query string. And then sorts it by code
     * point
     * 
     * @param canonicalQueryString
     * @return the query string
     */
    public static String mapToQueryString(Map<String, String> canonicalQueryString) {
        var map = canonicalQueryString.entrySet().stream() //
                .collect(Collectors.toMap(key -> uriEncode(key.getKey(), true), //
                        value -> uriEncode(value.getValue(), true)));
        // sort after encoding
        return new TreeMap<String, String>(map).entrySet().stream() //
                .map(mapper -> mapper.getKey() + "=" + mapper.getValue()) //
                .collect(Collectors.joining("&"));
    }

    public static String mapToSignedHeaders(Map<String, String> canonicalHeaders) {
        Map<String, String> sortedHeaders = new TreeMap<String, String>(canonicalHeaders);
        // collect headers used in signature
        return sortedHeaders.entrySet().stream() //
                .map(mapper -> mapper.getKey().toLowerCase()).collect(Collectors.joining(";"));
    }

    public static String mapToHeaders(Map<String, String> canonicalHeaders) {
        Map<String, String> sortedHeaders = new TreeMap<String, String>(canonicalHeaders);
        // collect headers used in signature
        return sortedHeaders.entrySet().stream() //
                .map(mapper -> {
                    return mapper.getKey().toLowerCase() + ":" + mapper.getValue().trim() + "\n";
                }).collect(Collectors.joining());
    }

    /**
     * Calculates signature V4
     * 
     * @param secretAccessKey
     * @param ISODate
     * @param awsRegion
     * @param awsService
     * @param stringToSign
     * @return
     */
    public static String signature(String secretAccessKey, String ISODate, String awsRegion, //
            String awsService, String stringToSign) {
        Objects.requireNonNull(secretAccessKey, "The `secretAccessKey` is required");
        Objects.requireNonNull(ISODate, "The `ISODate` is required");
        Objects.requireNonNull(awsRegion, "The `awsRegion` is required");
        Objects.requireNonNull(awsService, "The `awsService` is required");
        Objects.requireNonNull(stringToSign, "The `stringToSign` is required");

        var secretKey = ("AWS4" + secretAccessKey).getBytes(UTF_8);
        var dateKey = HMACSHA256(secretKey, ISODate.getBytes(UTF_8));
        var dateRegionKey = HMACSHA256(dateKey, awsRegion.getBytes(UTF_8));
        var dateRegionServiceKey = HMACSHA256(dateRegionKey, awsService.getBytes(UTF_8));
        var signingKey = HMACSHA256(dateRegionServiceKey, "aws4_request".getBytes(UTF_8));
        var signature = HMACSHA256(signingKey, stringToSign.getBytes(UTF_8));
        return hex(signature);
    }

    /**
     * Create authorization header
     * 
     * @param accessKeyId
     * @param ISODate
     * @param awsRegion
     * @param awsService
     * @param signedHeaders
     * @param signature
     * @return V4 Authorization header
     */
    public static String authorization(String accessKeyId, String ISODate, String awsRegion, //
            String awsService, String signedHeaders, String signature) {
        String credential = credential(accessKeyId, ISODate, awsRegion, awsService);
        return new StringBuilder().append("AWS4-HMAC-SHA256").append(" ") //
                .append("Credential=").append(credential).append(",") //
                .append("SignedHeaders=").append(signedHeaders).append(",") //
                .append("Signature=").append(signature) //
                .toString();
    }

    public static String credential(String accessKeyId, String ISODate //
            , String awsRegion, String awsService) {
        return accessKeyId + "/" + ISODate + "/" + awsRegion + "/" + awsService + "/aws4_request";
    }

    /**
     * Custom encoding to conform to AWS S3 implementation
     * 
     * @param input
     * @param encodeSlash whether slash should be encoded or not
     * @return
     */
    public static String uriEncode(CharSequence input, boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_'
                    || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(toHexUTF8(ch));
            }
        }
        return result.toString();
    }

    /**
     * Create a Canonical Request
     * 
     * @param httpVerb             is one of the HTTP methods, for example GET, PUT,
     *                             HEAD, and DELETE
     * @param canonicalURI         is the URI-encoded version of the absolute path
     *                             component of the URI—everything starting with the
     *                             "/" that follows the domain name and up to the
     *                             end of the string or to the question mark
     *                             character ('?') if you have query string
     *                             parameters. The URI in the following example,
     *                             /examplebucket/myphoto.jpg, is the absolute path
     *                             and you don't encode the "/" in the absolute
     *                             path:
     *                             {@code http://s3.amazonaws.com/examplebucket/myphoto.jpg}
     * @param canonicalQueryString specifies the URI-encoded query string
     *                             parameters. You URI-encode name and values
     *                             individually. You must also sort the parameters
     *                             in the canonical query string alphabetically by
     *                             key name. The sorting occurs after encoding. The
     *                             query string in the following URI example is
     *                             prefix=somePrefix&marker=someMarker&max-keys=20:
     *                             {@code http://s3.amazonaws.com/examplebucket?prefix=somePrefix&marker=someMarker&max-keys=20}
     *                             <p>
     *                             The canonical query string is as follows (line
     *                             breaks are added to this example for
     *                             readability):
     * 
     *                             <pre>
     * <code>
     * UriEncode("marker")+"="+UriEncode("someMarker")+"&"+
     * UriEncode("max-keys")+"="+UriEncode("20") + "&" +
     * UriEncode("prefix")+"="+UriEncode("somePrefix")
     * </code>
     *                             </pre>
     * 
     *                             <p>
     *                             If the URI does not include a '?', there is no
     *                             query string in the request, and you set the
     *                             canonical query string to an empty string ("").
     *                             You will still need to include the "\n".
     * @param canonicalHeaders     is a list of request headers with their values.
     *                             Individual header name and value pairs are
     *                             separated by the newline character ("\n"). Header
     *                             names must be in lowercase. You must sort the
     *                             header names alphabetically to construct the
     *                             string, as shown in the following example:
     * 
     *                             <pre>
     * <code>
     * Lowercase("HeaderName1")+":"+Trim("value")+"\n"
     * Lowercase("HeaderName2")+":"+Trim("value")+"\n"
     * ...
     * Lowercase(<HeaderNameN>)+":"+Trim(<value>)+"\n"
     * </code>
     *                             </pre>
     * 
     *                             The CanonicalHeaders list must include the
     *                             following:
     *                             <ul>
     *                             <li>HTTP host header</li>
     * 
     *                             <li>If the Content-Type header is present in the
     *                             request, you must add it to the CanonicalHeaders
     *                             list</li>
     * 
     *                             <li>Any {@code x-amz-*} headers that you plan to
     *                             include in your request must also be added. For
     *                             example, if you are using temporary security
     *                             credentials, you need to include
     *                             x-amz-security-token in your request. You must
     *                             add this header in the list of
     *                             CanonicalHeaders.</li>
     *                             </ul>
     * @param payload              an empty string if the request does not provide
     *                             payload
     * @return the canonical request
     */
    public static String canonicalRequest(String httpVerb, String canonicalURI, //
            @Nullable Map<String, String> canonicalQueryString, Map<String, String> canonicalHeaders, byte[] payload) {
        Objects.requireNonNull(httpVerb, "The `httpVerb` is required");
        Objects.requireNonNull(canonicalURI, "The `canonicalURI` is required");
        Objects.requireNonNull(canonicalHeaders, "The `canonicalHeaders` is required");
        Objects.requireNonNull(payload, "The `payload` is required");
        var queryString = "";
        // encode canonical URI - do not encode '/'
        var uri = uriEncode(canonicalURI, false);
        // expand, encode and sort canonical query string
        if (canonicalQueryString != null) {
            var map = canonicalQueryString.entrySet().stream() //
                    .collect(Collectors.toMap(key -> uriEncode(key.getKey(), true), //
                            value -> uriEncode(value.getValue(), true)));
            // sort after encoding
            queryString = new TreeMap<String, String>(map).entrySet().stream() //
                    .map(mapper -> mapper.getKey() + "=" + mapper.getValue()) //
                    .collect(Collectors.joining("&"));
        }
        Map<String, String> sortedHeaders = new TreeMap<String, String>(canonicalHeaders);
        // expand and trim canonical headers
        var headers = sortedHeaders.entrySet().stream() //
                .map(mapper -> {
                    return mapper.getKey().toLowerCase() + ":" + mapper.getValue().trim() + "\n";
                }).collect(Collectors.joining());
        // collect headers used in signature
        var signedHeaders = sortedHeaders.entrySet().stream() //
                .map(mapper -> mapper.getKey().toLowerCase()).collect(Collectors.joining(";"));
        var hashedPayload = hex(SHA256Hash(payload));
        return new StringBuilder().append(httpVerb).append('\n') //
                .append(uri).append('\n') //
                .append(queryString).append('\n') //
                .append(headers).append('\n') //
                .append(signedHeaders).append('\n') //
                .append(hashedPayload).toString();
    }

    /**
     * The canonical request for presigned URI
     * 
     * @param httpVerb
     * @param canonicalURI
     * @param canonicalQueryString
     * @param canonicalHeaders
     * @param canonicalSignedHeaders
     * @param payload
     * @return
     */
    public static String canonicalRequest(String httpVerb, String canonicalURI, //
            @Nullable String canonicalQueryString, String canonicalHeaders, //
            String canonicalSignedHeaders, byte[] payload) {
        Objects.requireNonNull(httpVerb, "The `httpVerb` is required");
        Objects.requireNonNull(canonicalURI, "The `canonicalURI` is required");
        Objects.requireNonNull(canonicalHeaders, "The `canonicalHeaders` is required");
        Objects.requireNonNull(canonicalSignedHeaders, "The `canonicalSignedHeaders` is required");
        var queryString = "";
        // encode canonical URI - do not encode slash ('/')
        var uri = uriEncode(canonicalURI, false);
        // expand, encode and sort canonical query string
        if (canonicalQueryString != null) {
            queryString = canonicalQueryString;
        }

        String hashedPayload = "UNSIGNED-PAYLOAD";
        if (payload != null) {
            hashedPayload = hex(SHA256Hash(payload));
        }
        return new StringBuilder().append(httpVerb).append('\n') //
                .append(uri).append('\n') //
                .append(queryString).append('\n') //
                .append(canonicalHeaders).append('\n') //
                .append(canonicalSignedHeaders).append('\n') //
                .append(hashedPayload).toString();
    }

    /**
     * Generates a SHA256 hash
     * 
     * @param payload the payload must be encoded in UTF-8
     * @return
     */
    public static byte[] SHA256Hash(byte[] payload) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(payload);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts hash to hex
     * 
     * @param hash hash
     * @return
     */
    public static String hex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * generate the string that requires signing
     * 
     * @param timeStamp
     * @param region
     * @param service
     * @param canonicalRequest
     * @return string to sign
     */
    public static String stringToSign(String ISOdate, String ISOtimeStamp, String region, String service, String canonicalRequest) {
        Objects.requireNonNull(canonicalRequest, "The `canonicalRequest` is required");
        var scope = ISOdate + "/" + region + "/" + service + "/aws4_request";
        var canonicalRequestHash = hex(SHA256Hash(canonicalRequest.getBytes(UTF_8)));
        return new StringBuilder().append("AWS4-HMAC-SHA256").append('\n') //
                .append(ISOtimeStamp).append('\n') //
                .append(scope).append('\n') //
                .append(canonicalRequestHash) //
                .toString();
    }

    /**
     * Generates a HMAC-SHA256 hash
     * 
     * @param secretKey
     * @param message
     * @return
     */
    public static byte[] HMACSHA256(byte[] secretKey, byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);
            return mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
    }
}

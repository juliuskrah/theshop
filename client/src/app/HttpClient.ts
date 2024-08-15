import Axios, { AxiosError, AxiosInstance, AxiosResponse } from "axios";

/**
 * Wrapper for axios: adapted from https://levelup.gitconnected.com/enhance-your-http-request-with-axios-and-typescript-f52a6c6c2c8e
 *
 * @author Julius Krah
 */
export class HttpClient {
  private static instance: HttpClient;
  private readonly axios: AxiosInstance;

  private constructor() {
    // axios will fetch the token automatically from either the cookie or the header
    this.axios = Axios.create({
      xsrfCookieName: "XSRF-TOKEN",
      xsrfHeaderName: "X-XSRF-TOKEN",
      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    });

    this._initializeResponseInterceptor();
  }

  /**
   * The static method that controls the access to the singleton instance.
   *
   * This implementation let you subclass the HttpClient class while keeping
   * just one instance of each subclass around.
   */
  public static getInstance(): HttpClient {
    if (!HttpClient.instance) {
      HttpClient.instance = new HttpClient();
    }

    return HttpClient.instance;
  }

  /**
   * Adds an interceptor that destructures `data` from response
   */
  private _initializeResponseInterceptor = () => {
    this.axios.interceptors.response.use(
      this._handleResponse,
      this._handleError
    );
  };

  /**
   * When a response is received, we destructure the data and ignore everything else
   */
  private _handleResponse = ({ data }: AxiosResponse) => data;

  /**
   * Handles errors when calling the endpoint
   * 
   * @param error the axios error intance
   */
  protected _handleError = (error: AxiosError) => Promise.reject(error);

  /**
   * Uploads a multipart form
   * @param url the post endpoint
   * @param formData the multipart form
   */
  public uploadImage = (url: string, file: File) =>
    this.axios.put(url, file, {
      headers: {
        "X-Amz-Acl": "bucket-owner-full-control",
        "Content-Type": file.type
      }
    });

  /**
   * Fetches a thymeleaf fragment
   * 
   * @param url the url the fragment
   * @param params the search parameters
   */
  public fetchFragment = (url: string, params?: any) =>
    this.axios.get(url, { params: params });

  /**
   * Used to request a presigned URL from the backend. This method receives an S3 presigned URL
   * 
   * @param url the endpoint that returns the presigned URL
   * @param filename the filename used for signing the URL
   * @param contentType the file contentType
   */
  public fetchPresignedUrl = (
    url: string,
    filename: string,
    contentType: string
  ) =>
    this.axios.get(url, {
      params: {
        filename: filename,
        contentType: contentType
      }
    });
}

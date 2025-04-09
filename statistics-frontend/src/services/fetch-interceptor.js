import fetchIntercept from 'fetch-intercept';
import { CONFIG } from "@/config.js";
import router from "@/router/index.js";

export class FetchInterceptor {
  static singletonInstance;
  sessionService;
  unregister;

  constructor(sessionService) {
    FetchInterceptor.singletonInstance = this;
    this.sessionService = sessionService;
    this.unregister = fetchIntercept.register(this);
  }

  request(url, options) {
    let token = FetchInterceptor.singletonInstance.sessionService.getTokenFromBrowserStorage();

    // Check if token is null or the URL is not from the backend
    if (token == null || !url.startsWith(CONFIG.BACKEND_URL)) {
      // No changes for non-authenticated requests
      return [url, options];
    }

    // Handle cases where options are null
    if (options == null) {
      // Authorization header is the only custom option
      return [url, { headers: { Authorization: token } }];
    } else {
      // Add Authorization header to other options
      let newOptions = { ...options };
      newOptions.headers = { ...newOptions.headers, Authorization: token };
      return [url, newOptions];
    }
  }

  requestError(error) {
    return Promise.reject(error);
  }

  response(response) {
    // FetchInterceptor.singletonInstance.tryRecoverNewJWToken(response);
    // Trap some specific HTTP error responses
    if (response.status >= 400 && response.status < 600) {
      FetchInterceptor.singletonInstance.handleErrorInResponse(response);
    }
    return response;
  }

  responseError(error) {
    return Promise.reject(error);
  }

  handleErrorInResponse(response) {
    console.log(response.status);
    if (response.status >= 500) {
      console.error("Server error occurred. Please try again later.");
    } else if (response.status === 404) {
      console.error("Requested resource not found.");
    } else if (response.status === 403) {
      console.error("You do not have permission to access this resource.");
    } else if (response.status === 401) {
      console.error("Unauthorized - please log in");
      this.sessionService.saveTokenIntoBrowserStorage(null)
    } else if (response.status >= 400) {
      console.error("A client error occurred. Please check your request." + response.toString());
    }
  }
}

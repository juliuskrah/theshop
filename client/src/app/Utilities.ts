import { AxiosError } from "axios";
import { HttpClient } from "./HttpClient";
import * as htmx from "htmx.org";

/**
 * @author Julius Krah
 */
export class Utilities {
  public static loadFragment(url: string, params: any = {}, target = "form") {
    const httpClient = HttpClient.getInstance();
    const searchParams = new URLSearchParams(window.location.search);

    for (const [key, value] of Object.entries(params)) {
      searchParams.append(key, value as string);
    }

    httpClient
      .fetchFragment(url, searchParams)
      .then(data => {
        this.replaceElementInplaceWithFragment(target, data);
      })
      .catch((error: AxiosError) => {
        console.error(error);
      });
  }

  private static replaceElementInplaceWithFragment(target: string, data: any) {
    const div = document.getElementById(target);
    const svg = document.getElementById("product-content-loader");
    if (div) {
      div.innerHTML = data;
      svg!.setAttribute("class", "hidden");
      // Let's execute the script from the response
      const script = div.getElementsByTagName("script");
      for (let n = 0; n < script.length; n++) {
        if (script[n].type === "text/javascript" || script[n].type === "") {
          try {
            eval(script[n].innerText);
          } catch (e) {
            if (console.error) {
              console.error(e);
            } else if (console.log) {
              console.log("ERROR: ", e);
            }
          }
        }
      }
    }
  }

  /**
   * Remove unnecessary search parameters and add role=list
   */
  private static buildPaginationUrl(): URL {
    const url = new URL(window.location.href);
    url.searchParams.delete("before");
    url.searchParams.delete("after");
    url.searchParams.delete("role");
    url.searchParams.append("role", "list");
    return url;
  }

  /**
   * Register events for htmx
   *
   * @see https://htmx.org/events/
   */
  private static registerHtmxEvents() {
    // htmx does not send the X-Requested-With header. Let's add it
    htmx.on("htmx:configRequest", function(event: any) {
      if (event.detail.headers) {
        event.detail.headers["X-Requested-With"] = "XMLHttpRequest";
      }
    });
    // Add hx-push-url to push the history state
    htmx.on("htmx:beforeOnLoad", function(event: any) {
      if (event.detail.elt) {
        event.detail.elt.setAttribute("hx-push-url", true);
      }
    });
    // Let's modify the URL pushed to history
    htmx.on("htmx:beforeHistorySave", function(event: any) {
      // Not working as expected...
      // const location = window.location;
      // const path = `${location.protocol}//${location.host}${event.detail.path}`
      // console.log("Path", path);
      // const url = new URL(path);
      // url.searchParams.delete("role");
      // event.detail.path = `${url.pathname}${url.search}`;
    });
  }

  static paginationNavigateForward(after: string) {
    const url = Utilities.buildPaginationUrl();
    url.searchParams.append("after", after);
    Utilities.registerHtmxEvents();
    htmx.ajax("GET", url.href, "#list");
  }

  static paginationNavigateBackward(before: string) {
    const url = Utilities.buildPaginationUrl();
    url.searchParams.append("before", before);
    Utilities.registerHtmxEvents();
    htmx.ajax("GET", url.href, "#list");
  }
}

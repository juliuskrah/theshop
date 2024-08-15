import { AxiosError } from "axios";
import { HttpClient } from "./HttpClient";

/**
 * @author Julius Krah
 */
export class ProductAdmin {
  static uri: string;

  private dropArea: HTMLElement | null = document.getElementById(
    "product-drop-area-s-01"
  );

  /**
   * Handles the files that are dropped in the drop area
   * 
   * @param files the files to upload
   */
  public static handleFiles(files?: FileList) {
    [...files!].forEach(file => {
      ProductAdmin.uploadProductImage(file);
      ProductAdmin.previewFile(file);
    });
  }

  /**
   * Drag and Drop operation
   */
  public dragAndDrop() {
    this.dropArea?.addEventListener(
      "dragenter",
      this.handleDragDropEvent,
      false
    );
    this.dropArea?.addEventListener(
      "dragover",
      this.handleDragDropEvent,
      false
    );
    this.dropArea?.addEventListener(
      "dragleave",
      this.handleDragDropEvent,
      false
    );
    this.dropArea?.addEventListener("drop", this.handleDragDropEvent, false);

    ["dragenter", "dragover"].forEach(eventName => {
      this.dropArea?.addEventListener(eventName, this.highlight, false);
    });
    ["dragleave", "drop"].forEach(eventName => {
      this.dropArea?.addEventListener(eventName, this.unhighlight, false);
    });
  }

  /**
   * This function sets up a one way databinding. Anything entered into the name text field
   * will appear in the friendlyName text field
   */
  public dataBindFriendlyName() {
    const name = document.getElementById("name") as HTMLInputElement;
    const friendlyName = document.getElementById(
      "friendlyName"
    ) as HTMLInputElement;
    name.addEventListener("keyup", (event: KeyboardEvent) => {
      const str = name.value.toLowerCase();
      const join = str.match(/[^ -]+/g)?.join("-");
      if (typeof join !== "undefined") {
        friendlyName.value = join;
      } else {
        friendlyName.value = "";
      }
    });
  }

  /**
   * Handles the drag and drop events
   * 
   * @param event the DragEvent
   * @see DragEvent
   */
  private handleDragDropEvent(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    if (event.type === "drop") {
      ProductAdmin.handleDrop(event as DragEvent);
    }
  }

  /**
   * Handles the drop event
   * 
   * @param event the DragEvent
   * @see handleDragDropEvent
   */
  private static handleDrop(event: DragEvent) {
    const dataTransfer = event.dataTransfer;
    const files = dataTransfer?.files;
    ProductAdmin.handleFiles(files);
  }

  /**
   * Highlights the drop area when the mouse comes into focus
   * 
   * @param this the current Element
   * @param event the drop event
   */
  private highlight(this: HTMLElement, event: Event) {
    this.classList.add("border-blue-500");
  }

  /**
   * Removes highlight from the drop area when the mouse leaves
   * 
   * @param this the current Element
   * @param event the drop event
   */
  private unhighlight(this: HTMLElement, event: Event) {
    this.classList.remove("border-blue-500");
  }

  /**
   * Enables preview of the images added
   * 
   * @param file the image file
   */
  private static previewFile(file: File) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = function() {
      const img = document.createElement("img");
      img.src = reader.result as string;
      img.alt = file.name;
      img.className = "flex-1 object-contain";
      document.getElementById("product-gallery")?.appendChild(img);
    };
  }

  /**
   * Makes a request to the server to fetch the presigned URI. When successful, it uses this URI to
   * upload to AWS S3
   * 
   * @param file to upload
   */
  private static async uploadProductImage(file: File) {
    const httpClient = HttpClient.getInstance();
    try {
      const presignedUri = await httpClient.fetchPresignedUrl(
        ProductAdmin.uri,
        file.name,
        file.type
      );
      const uri = (presignedUri as unknown) as string;
      const uploadPart = await httpClient.uploadImage(uri, file);
      const mediaSrc = document.getElementById("mediaSrc");
      if (uri.includes("upload-media")) {
        (mediaSrc as HTMLInputElement).value = (uploadPart as unknown) as string;
      } else {
        // Remove query parameters that contain signature
        (mediaSrc as HTMLInputElement).value = uri.split(/[?#]/)[0];
      }
    } catch (error) {
      this.handleFileUploadError(error as AxiosError);
    }
  }

  /**
   * Handles error related to file upload 
   * 
   * @param error the axios error
   */
  private static handleFileUploadError(error: AxiosError) {
    const alert = document.getElementById("product-alert");
    // make alert visible
    alert?.classList.remove("invisible");
    // style it as danger
    alert?.classList.add("text-red-700", "bg-red-100");
    const p = alert?.querySelector("p");
    p!.innerHTML = error.message;
    setTimeout(() => {
      // remove danger styling after timeout
      alert?.classList.remove("text-red-700", "bg-red-100");
      // hide
      alert?.classList.add("invisible");
    }, 30000);
  }
}

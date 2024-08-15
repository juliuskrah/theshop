import { ProductAdmin } from "./ProductAdmin";
import { Utilities } from "./Utilities";

/**
 * The Shoperal class is exposed at the client layer. It consist mainly of static classes that delegates
 * tasks to appropraite classes
 *
 * @author Julius Krah
 */
export class Shoperal {
  static handleProductUpload(files: FileList, uri: string): void {
    ProductAdmin.uri = uri;
    ProductAdmin.handleFiles(files);
  }

  static handleMoveAfter(after: string) {
    Utilities.paginationNavigateForward(after);
  }

  static handleMoveBefore(before: string) {
    Utilities.paginationNavigateBackward(before);
  }

  static intializeProductAdminForm(uri: string): void {
    ProductAdmin.uri = uri;
    const product = new ProductAdmin();
    product.dragAndDrop();
    product.dataBindFriendlyName();
  }

  static loadFragment(url: string, params?: any, target?: string) {
    Utilities.loadFragment(url, params, target);
  }
}

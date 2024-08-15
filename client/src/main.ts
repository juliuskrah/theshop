import { Shoperal } from "./app/Shoperal";

import "./assets/sass/shoperal.scss";

declare global {
  interface Window {
    htmx: any;
    Shoperal: any;
  }
}

window.Shoperal = Shoperal;
window.htmx = {};
window.htmx = require("htmx.org");

import {Application} from './application';
import '../css/tailwind.css';

const anchors: HTMLCollectionOf<HTMLAnchorElement> = document.getElementsByTagName(
  'a'
);

(() => {
  navigate({from: 'Shops'}); // this is invoked on application start to register event listeners
})();

function navigate(app: Application) {
  for (const anchor of Array.from(anchors)) {
    console.log('Starting...', app.from);
    anchor.addEventListener('click', click);
  }
}

function click(this: HTMLAnchorElement, evt: MouseEvent) {
  evt.preventDefault();
  console.log('Navigating to', this.href);
}

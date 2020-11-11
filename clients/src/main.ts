import Vue from 'vue'
import './assets/sass/shop.scss'

declare global {
    interface Window { Vue: any; }
}

Vue.config.productionTip = false
Vue.component('top-menu', require('./components/TopMenu.vue').default);
Vue.component('product-card', require('./components/ProductCard').default);
window.Vue = Vue;
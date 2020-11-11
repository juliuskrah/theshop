import Vue from 'vue'
import './assets/sass/shop.scss'

Vue.config.productionTip = false
Vue.component('top-menu', require('./components/TopMenu.vue').default);
window.Vue = Vue;
import TopMenu from "../components/TopMenu.vue";
import '../assets/sass/shop.scss'

export default {
  title: "TopMenu",
  component: TopMenu,
};

export const DefaultState = () => ({
  components: {
    topMenu: TopMenu,
  },
  template: `<top-menu />`,
});

import ProductCard from "../components/ProductCard.vue";
import { storiesOf } from '@storybook/vue';
import '../assets/sass/shop.scss'
import { Product } from '../model/Product';

export const product: Product = {
    id: "1",
    name: "Nike",
    description: `Lorem ipsum dolor sit amet consectetur adipisicing elit. Modi quos
                  quidem sequi illum facere recusandae voluptatibus`,
    imageSrc: new URL("https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80"),
    price: 129.99,
}
storiesOf('Product Card', module)
    .add('default', () => ({
        components: { ProductCard },
        template: `<product-card :product=product />`,
        data: () => ({ product }),
    }));

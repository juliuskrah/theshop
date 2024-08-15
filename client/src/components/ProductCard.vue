<template>
  <div class="max-w-xs bg-white shadow-lg rounded-lg overflow-hidden my-10">
    <div class="px-4 py-2">
      <h1 class="text-gray-900 font-bold text-3xl uppercase">
        {{ product.name }}
      </h1>
      <p class="text-gray-600 text-sm mt-1">
        {{ product.description }}
      </p>
    </div>
    <img
      class="h-56 w-full object-cover mt-2"
      :src="product.imageSrc"
      :alt="product.name"
    />
    <div class="flex items-center justify-between px-4 py-2 bg-gray-900">
      <h1 class="text-gray-200 font-bold text-xl">${{ product.price }}</h1>
      <button
        class="px-3 py-1 bg-gray-200 text-sm text-gray-900 font-semibold rounded"
      >
        Add to cart
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import { Product } from "../model/Product";

@Component
export default class ProductCard extends Vue {
  @Prop({ required: true }) private product!: Product;
}
</script>

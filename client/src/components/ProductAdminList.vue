<template>
  <div class="flex-grow overflow-auto">
    <div>
      No products found. Click to
      <a
        class="py-2 transition-colors duration-200 relative hover:text-gray-900 text-gray-500"
        href="/admin/product/add"
        >add product</a
      >
    </div>
    <a
      href="#"
      data-th-href="@{/admin/product/add}"
      class="bg-blue-500 rounded-full font-bold text-white px-4 py-3 transition duration-300 ease-in-out hover:bg-blue-600 mr-6"
    >
      Add Product
    </a>
    <table class="shoperal-table">
      <thead>
        <tr>
          <th class="shoperal-table-head-row cursor-pointer">Name</th>
          <th class="shoperal-table-head-row">Handle</th>
          <th class="shoperal-table-head-row">Action</th>
        </tr>
      </thead>
      <tbody class="shoperal-table-body">
        <tr>
          <td class="shoperal-table-body-row">Freda's</td>
          <td class="shoperal-table-body-row">freda</td>
          <td class="shoperal-table-body-row">delete</td>
        </tr>
      </tbody>
    </table>
    <div class="flex text-gray-700">
      <div
        class="h-12 w-12 mr-1 flex justify-center items-center rounded-full bg-gray-200 cursor-pointer"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="100%"
          height="100%"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="feather feather-chevron-left w-6 h-6"
        >
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <div
        class="disabled:opacity-50 h-12 w-12 ml-1 flex justify-center items-center rounded-full bg-gray-200 cursor-not-allowed"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="100%"
          height="100%"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="feather feather-chevron-right w-6 h-6"
        >
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </div>
    </div>
  </div>
</template>

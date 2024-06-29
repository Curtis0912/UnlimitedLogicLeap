<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useRoute } from "vue-router";
import { onMounted } from "vue";

const route = useRoute();

/**
 * 全局初始化函数，有全局单次调用的代码，写在这里
 */
const doInit = () => {
  console.log("welcome to uul");
}
onMounted(() => {
  doInit();
})
</script>

<style>
#app {
}
</style>

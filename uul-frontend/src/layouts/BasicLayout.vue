<template>
  <div id="basicLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">By Curtis</a-layout-footer>
    </a-layout>
  </div>
</template>

<script>
import GlobalHeader from "@/components/GlobalHeader";
export default {
  name: "BasicLayout",
  components: { GlobalHeader },
};
</script>

<style scoped>
#basicLayout {
}

.header {
  margin-bottom: 15px;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  max-width: 1200px;
  width: 100%;
  box-sizing: border-box;
  margin: 0 auto 28px;
  background: linear-gradient(to right, #fefefe, #fff);
  padding: 20px;
}

.footer {
  background-color: #efefef;
  text-align: center;
  padding: 15px;
}
</style>

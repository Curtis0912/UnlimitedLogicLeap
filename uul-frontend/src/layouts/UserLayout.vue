<template>
  <div id="userlayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space class="space">
          <img src="https://ull-1327358678.cos.ap-guangzhou.myqcloud.com/logo.png" class="logo" />
          <div>
<!--            <div>UnlimitedLogicLeap</div>-->
            <div>UUL智能答题应用平台</div>
          </div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <div>天下之事，困于想，而破于行</div>
        <div>By Curtis</div>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script>
export default {
  name: "UserLayout",
};
</script>

<style scoped>
#userlayout {
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")
    0% 0% / 100% 100%;
}

.logo {
  width: 48px;
}

.header {
  margin-bottom: 15px;
  /*box-shadow: #eee 1px 1px 5px;*/
  font-size: 21px;
}

.space {
  margin-top: 30px;
}

.content {
  margin-bottom: 28px;
  padding: 20px;
}

.footer {
  background-color: #efefef;
  text-align: center;
  padding: 15px;
}
.footer div {
  margin-bottom: 5px;
}
</style>

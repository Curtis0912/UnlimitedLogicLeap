<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="500px">
      <div class="titleBar">
        <img src="https://ull-1327358678.cos.ap-guangzhou.myqcloud.com/logo.png" class="logo" />
        <!--            <div class="title">UUL智能答题平台</div>-->
        <h3 style="margin-left: 16px">智跃无限，问答未来——UUL，让知识与你同频共振！</h3>
      </div>
    </a-col>
    <a-col flex="auto">

      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >

        <a-menu-item v-for="item in visibleRoutes" :key="item.path" class="menu_controller">
          <div class="menu_div">
            {{ item.name }}
          </div>
        </a-menu-item>

      </a-menu>
    </a-col>
    <a-col flex="100px">
      <a-dropdown v-if="loginUserStore.loginUser.id" trigger="hover">
        <a-button>{{ loginUserStore.loginUser.userName ?? "无名" }}</a-button>
        <template #content>
          <div>
            <a-button @click="doUserInfo">个人信息</a-button>
          </div>
          <div>
            <a-button @click="toBeVIP">打赏作者</a-button>
          </div>
          <div>
            <a-button @click="logoutUser">退出登录</a-button>
          </div>
        </template>
      </a-dropdown>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
  <UserInfoModal ref="userInfoRef" :id="loginUserStore.loginUser.id" title="个人信息"/>
  <ToBeVIPModal ref="toBeVIPRef" title="感谢支持"/>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref, watchEffect } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
import { userLogoutUsingPost } from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import UserInfoModal from "@/components/UserInfoModal.vue";
import ToBeVIPModal from "@/components/ToBeVIPModal.vue";
const router = useRouter();

const loginUserStore = useLoginUserStore();

//Tab栏选中菜单项
const selectedKeys = ref(["/"]);

//路由跳转后，更新选中的菜单项
router.afterEach((to) => {
  selectedKeys.value = [to.path];
});

//点击菜单跳转到对应页面
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

//展示在菜单的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    //根据权限过滤菜单
    if (!checkAccess(loginUserStore.loginUser,item.meta?.access as string)) {
      return false;
    }
    return true;
  });
})

//个人信息表单引用
const userInfoRef = ref();

//个人信息
const doUserInfo = (e: Event) => {
  if (userInfoRef.value) {
    userInfoRef.value.openModal();
  }
}

//成为会员引用
const toBeVIPRef = ref();
//成为会员
const toBeVIP = () => {
  if (toBeVIPRef.value) {
    toBeVIPRef.value.openModal();
  }
}

/**
 * 退出登录
 */
const logoutUser = async () => {
  const res = await userLogoutUsingPost();
  if (res.data.code === 0) {
    message.success("退出登录成功");
    router.push('/user/login');
  } else {
    message.error("退出登录失败，" + res.data.message);
  }
}

</script>

<style scoped>
#globalHeader {
}
.menu_controller {
  width: 80px;
  height: 50px;
}
.menu_div {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
}
.titleBar {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.title {
  color: black;
  margin-left: 15px;
}

.logo {
  width: 48px;
}
</style>

<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 50px">用户登录</h2>
    <a-form :model="form" style=" width: 400px; margin:0 auto " @submit="handleSubmit" auto-label-width label-align="left">
      <a-form-item field="userAccount" tooltip="Please enter userAccount" label="userAccount">
        <a-input
          v-model="form.userAccount"
          placeholder="please enter your userAccount"
        />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不能少于8位" label="userPassword">
        <a-input-password v-model="form.userPassword" placeholder="please enter your userPassword" />
      </a-form-item>
      <a-form-item>
        <div style="width: 100%;display: flex;align-items: center;justify-content: space-between">
          <a-button type="primary" style="width: 120px" html-type="submit">Login</a-button>
          <a-link href="/user/register">Register</a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import API from "@/api";
import { userLoginUsingPost } from "@/api/userController";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";

/**
 * 表单信息
 */
const form = reactive({
  userAccount: "",
  userPassword: ""
} as API.UserLoginRequest);

const router = useRouter();
const loginUserStore = useLoginUserStore();
/**
 * 提交表单
 * @param data
 */
const handleSubmit = async () => {
  const res = await userLoginUsingPost(form);
  //登录成功后跳转到主页
  if (res.data.code === 0) {
    await loginUserStore.fetchLoginUser();
    message.success("登录成功");
    router.push({
      path: "/",
      replace: true
    });
  } else {
    message.error("登录失败," + res.data.message);
  }
};

</script>

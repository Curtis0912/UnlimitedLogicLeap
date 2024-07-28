<template>
  <div id="userRegisterPage">
    <h2 style="margin-bottom: 50px">用户注册</h2>
    <a-form :model="form" style=" width: 400px; margin:0 auto " @submit="handleSubmit" auto-label-width label-align="left">
      <a-form-item field="userAccount" tooltip="请输入你的账号" label="账号" required hide-asterisk>
        <a-input
          v-model="form.userAccount"
          placeholder="请输入你的账号"
        />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不能少于8位" label="密码" required hide-asterisk>
        <a-input-password v-model="form.userPassword" placeholder="请输入你的密码" />
      </a-form-item>
      <a-form-item field="checkPassword" tooltip="验证密码不能少于8位" label="校验密码" required hide-asterisk>
        <a-input-password v-model="form.checkPassword" placeholder="请再次输入你的密码" />
      </a-form-item>
      <a-form-item>
        <div style="width: 100%;display: flex;align-items: center;justify-content: space-between">
          <a-button type="primary" style="width: 120px" html-type="submit">注册</a-button>
          <a-link href="/user/login">老用户登录</a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import API from "@/api";
import { userRegisterUsingPost } from "@/api/userController";
import { useRouter } from "vue-router";
import message from "@arco-design/web-vue/es/message";

/**
 * 表单信息
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as API.UserRegisterRequest);

const router = useRouter();
/**
 * 提交表单
 * @param data
 */
const handleSubmit = async () => {
  const res = await userRegisterUsingPost(form);
  //注册成功后跳转到登录页
  if (res.data.code === 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true
    });
  } else {
    message.error("注册失败," + res.data.message);
  }
};

</script>

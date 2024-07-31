<template>
  <a-modal v-model:visible="visible" title="Modal Form" @cancel="closeModal" :footer="false">
    <a-form @submit="handleSubmit" :model="form">
      <a-form-item field="id" label="应用 id" disabled>
        <a-input v-model="form.id" />
      </a-form-item>
      <a-form-item field="userName" label="用户名称">
        <a-input v-model="form.userName" />
      </a-form-item>
      <a-form-item field="userAvatar" label="应用图标">
        <PictureUploader biz="user_avatar" :value="form.userAvatar" :onChange="(value) => (form.userAvatar = value)" />
      </a-form-item>
      <a-form-item field="userProfile" label="用户描述">
        <a-input v-model="form.userProfile" />
      </a-form-item>
      <a-form-item field="userRole" label="用户角色" disabled>
        <a-input v-model="form.userRole" />
      </a-form-item>
      <div style="text-align: center">
        <a-button type="primary" html-type="submit">提交</a-button>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { defineExpose, defineProps, reactive, ref, watchEffect, withDefaults } from "vue";
import PictureUploader from "@/components/PictureUploader.vue";
import { getUserVoByIdUsingGet, updateMyUserUsingPost } from "@/api/userController";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";

const visible = ref(false);

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  }
});
/**
 * 表单信息
 */
const form = ref({
  id: props.id,
  userName: "",
  userAvatar: "",
  userProfile: "",
  userRole: ""
} as API.UserUpdateMyRequest);

const oldUserInfo = ref<API.UserVO>();
/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getUserVoByIdUsingGet({
    id: props.id as any
  });
  if (res.data.code === 0 && res.data.data) {
    oldUserInfo.value = res.data.data;
    console.log(oldUserInfo.value);
    form.value = { ...form.value, ...oldUserInfo.value };
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
//获取旧数据
watchEffect(() => {
  loadData();
});


//关闭弹窗
const closeModal = () => {
  visible.value = false;
};
//打开弹窗
const openModal = () => {
  visible.value = true;
};
/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (props.id) {
    const res = await updateMyUserUsingPost({
      ...form.value
    })
    if (res.data.code === 0) {
      message.success("更新成功");
      closeModal();
    } else {
      message.error("更新失败，" + res.data.message);
    }
  }
};
defineExpose({
  openModal
});
</script>

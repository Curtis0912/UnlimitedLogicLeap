<template>
  <a-modal v-model:visible="visible" title="成为尊贵的VIP" @cancel="closeModal" :footer="false">
    <div class="center">
      <img class="weixinpay" src="https://ull-1327358678.cos.ap-guangzhou.myqcloud.com/tobevip.jpg">
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { defineExpose, ref } from "vue";

const visible = ref(false);

//关闭弹窗
const closeModal = () => {
  visible.value = false;
};
//打开弹窗
const openModal = () => {
  visible.value = true;
};

defineExpose({
  openModal
});
</script>
<style>
.center {

  text-align: center;
}
.weixinpay {
  width: 200px;

  height: 280px;
}
</style>

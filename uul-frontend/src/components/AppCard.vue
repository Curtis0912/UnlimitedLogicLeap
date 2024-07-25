<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <template #actions>
<!--      <span class="icon-hover"> <IconThumbUp /> </span>-->
      <span class="icon-hover" @click="doShare"> <IconShareInternal /> </span>
    </template>
    <template #cover>
      <div
        :style="{
          height: '204px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{ width: '100%', height: '100%', transform: 'translateY(-20px)'}"
          :alt="app.appName"
          :src="app.appIcon"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc.slice(0,18)">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar :image-url="app.user?.userAvatar" :size="24" :style="{ marginRight: '8px' }">
            A
          </a-avatar>
          <a-typography-text>{{ app.user?.userName ?? 'somebody' }}</a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
  <ShareModal ref="shareModalRef" :link="shareLink" title="应用分享"/>
</template>

<script setup lang="ts">
import {
  IconThumbUp,
  IconShareInternal,
  IconMore,
} from '@arco-design/web-vue/es/icon';
import API from '@/api';
import { withDefaults, defineProps, ref, computed } from "vue";
import { useRouter } from "vue-router";
import ShareModal from "@/components/ShareModal.vue";


interface Props {
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(),{
  app: () => {
    return {};
  },
});

//分享弹窗引用
const shareModalRef = ref();
//分享链接
const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.app.id}`;
//分享
const doShare = (e: Event) => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
  //阻止冒泡，防止跳转到详情页
  e.stopPropagation();
}

const router = useRouter();

const doCardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
}


</script>
<style scoped>
.appCard {
  cursor: pointer;
  height: 330px;
}
.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}
.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}
</style>

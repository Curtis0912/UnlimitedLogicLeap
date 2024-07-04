<template>
  <div id="appDetailPage">
    <a-card>
      <a-row class="grid-demo" style="margin-bottom: 16px;">
        <a-col flex="auto" class="content">
          <h1>{{ data.appName }}</h1>
          <p>应用详情：{{ data.appDesc }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              作者：
          <div :style="{ display: 'flex',alignItems: 'center'}">
            <a-avatar
              :image-url="data.user?.userAvatar"
              :size="24"
              :style="{ marginRight: '8px' }"
            />
            <a-typography-text>{{ data.user?.userName ?? "somebody" }}</a-typography-text>
          </div>
          </a-space>
          </p>
          <p>创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}</p>
          <a-space size="medium">
            <a-button :href="`/answer/do/${id}`" type="primary">开始答题</a-button>
            <a-button>分享应用</a-button>
            <a-button v-if="isMy" :href="`/add/question/${id}`">设置题目</a-button>
            <a-button v-if="isMy" :href="`/add/scoring_result/${id}`">设置评分</a-button>
            <a-button v-if="isMy" :href="`/add/app/${id}`">修改应用</a-button>
          </a-space>
        </a-col>
        <a-col width="100%" flex="320px">
          <a-image width="64" :src="data.appIcon" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { withDefaults, defineProps } from "vue";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { useLoginUserStore } from "@/store/userStore";


interface Props {
  id: string,
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  }
});

const data = ref<API.AppVO>({});

//获取登录用户
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser?.id;
//是否为本人创建   computed当有值发生变化时重新计算
const isMy = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any
  });
  if (res.data.code === 0 && res.data.data) {
    data.value = res.data.data;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};


/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

</script>

<style>
#appDetailPage {

}

.content > * {
  margin-bottom: 24px;
}

</style>

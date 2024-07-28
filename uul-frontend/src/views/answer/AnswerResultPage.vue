<template>
  <div id="answerResultPage">
    <a-card>
      <a-row class="grid-demo" style="margin-bottom: 16px;">
        <a-col flex="600px" class="content">
          <h1>{{ data.resultName }}</h1>
          <p>结果描述：{{ data.resultDesc }}</p>
          <p>结果id：{{ data.resultId }}</p>
          <p>结果得分：{{ data.resultScore }}</p>
          <p>我的答案：{{ data.choices }}</p>
          <p>应用id：{{ data.appId }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              答题人：
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
          <p>答题时间：{{ dayjs(data.updateTime).format("YYYY-MM-DD HH:mm:ss") }}</p>
          <a-space size="medium">
            <a-button :href="`/answer/do/${data.appId}`" type="primary">重新答题</a-button>
          </a-space>
        </a-col>
        <a-col flex="500px" style="margin-left: 20px">
          <a-image width="500" :src="data.resultPicture" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import API from "@/api";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import message from "@arco-design/web-vue/es/message";
import { withDefaults, defineProps } from "vue";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

interface Props {
  id: string,
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  }
});

const data = ref<API.UserAnswerVO>({});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getUserAnswerVoByIdUsingGet({
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
#answerResultPage {

}

.content > * {
  margin-bottom: 24px;
}

</style>

<template>
  <div id="doAnswerPage">
    <a-card>
      <h1 class="title">{{ app.appName }}</h1>
      <p>{{ app.appDesc }}</p>
      <h2>
        {{ current }}、{{ currentQuestion?.title }}
      </h2>
      <a-radio-group v-model="currentAnswer" :options="questionOptions" @change="doRadioChange" />
      <div style="margin-top: 16px">
        <a-space size="large">
          <a-button
            type="primary"
            circle
            v-if="current < questionContent.length"
            :disabled="!currentAnswer"
            @click="current += 1"
          >
            下一题
          </a-button>
          <a-button
            type="primary"
            circle
            v-if="current === questionContent.length"
            :disabled="!currentAnswer"
            @click="doSubmit"
          >
            查看结果
          </a-button>
          <a-button
            circle
            v-if="current > 1"
            @click="current -= 1"
          >
            上一题
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watchEffect } from "vue";
import API from "@/api";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";

import { withDefaults, defineProps } from "vue";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";

interface Props {
  appId: string,
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  }
});

const app = ref<API.AppVO>({});
//题目内容结构（题目列表）
const questionContent = ref<API.QuestionContentDTO[]>([]);

//当前题目的序号（从1开始）
const current = ref(1);
//当前题目
const currentQuestion = ref<API.QuestionContentDTO>({});
//当前题目选项
const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value.options.map((option) => {
      return {
        label: `${option.key}.${option.value}`,
        value: option.key
      };
    })
    : [];
});
//当前答案
const currentAnswer = ref<string>();
//回答列表
const answerList = reactive<string[]>([]);

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  //获取app
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId as any
  });
  if (res.data.code === 0 && res.data.data) {
    app.value = res.data.data;
  } else {
    message.error("获取应用失败，" + res.data.message);
  }

  //获取题目
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",//按创建时间排序
    sortOrder: "descend"//降序排序
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data?.records[0].questionContent;
  } else {
    message.error("获取题目失败，" + res.data.message);
  }
};
//获取旧数据
watchEffect(() => {
  loadData();
});
//改变current题号后，会自动更新当前题目和答案
watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
  currentAnswer.value = answerList[current.value - 1];
});

/**
 * 选中选项后，保存选项记录
 */
const doRadioChange = (value: string) => {
  answerList[current.value - 1] = value;
};

const router = useRouter();
/**
 * 提交表单
 * @param data
 */
const doSubmit = async () => {
  if (!props.appId || !answerList) {
    return;
  }
  const res = await addUserAnswerUsingPost({
    appId: props.appId as any,
    choices: answerList
  });
  if (res.data.code === 0 && res.data.data) {
    router.push(`/answer/result/${res.data.data}`);
  } else {
    message.error("提交答案失败," + res.data.message);
  }
};

</script>

<style scoped>
#doAnswerPage {
  width: 800px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 32px;
}
</style>

<template>
  <a-button type="outline" @click="handleClick">AI 生成题目</a-button>
  <a-modal v-model:visible="visible" title="AI 生成题目" @cancel="handleCancel" @before-ok="handleBeforeOk" :footer="false">
    <a-form :model="form" @submit="handleSubmit">
      <a-form-item label="应用id">
        {{ appId }}
      </a-form-item>
      <a-form-item field="questionNumber" label="题目数量">
        <a-input-number min="0" max="20" v-model="form.questionNumber"  placeholder="请输入题目数量" />
      </a-form-item>
      <a-form-item field="optionNumber" label="选项数量">
        <a-input-number min="0" max="4" v-model="form.optionNumber"  placeholder="请输入选项数量" />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button :loading="submitting" type="primary" style="width: 120px;margin-left: 10px" html-type="submit">
            {{ submitting ? "生成中" : "一键生成" }}
          </a-button>
          <a-button :loading="ssesubmitting" type="primary" style="width: 120px;margin-left: 10px" @click="handleSSESubmit">
            {{ ssesubmitting ? "生成中" : "实时生成" }}
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { withDefaults, defineProps } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";


interface Props {
  appId: string,
  onSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSSESuccess?: (result: API.QuestionContentDTO) => void;
  onSSEStart?: (event: any) => void;
  onSSEClose?: (event: any) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  }
});
const visible = ref(false);
const submitting = ref(false);
const ssesubmitting = ref(false);
const form = reactive({
  questionNumber: 10,
  optionNumber: 2
} as API.AIGenerateQuestionRequest);

const handleClick = () => {
  visible.value = true;
};
// const handleBeforeOk = (done) => {
//   console.log(form);
//   window.setTimeout(() => {
//     done();
//     // prevent close
//     // done(false)
//   }, 3000);
// };
const handleCancel = () => {
  visible.value = false;
};
/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  submitting.value = true;
  const res:any = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form,
  })
  if (res.data.code === 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      //得到的结果传递给父组件
      props.onSuccess(res.data.data);
    } else {
      message.success("生成题目成功");
    }
    //关闭对话框
    handleCancel();
  } else {
    message.error("生成题目失败");
  }
  submitting.value = false;
}

/**
 * 提交 （SSE实时生成）
 */
const handleSSESubmit = async () => {
  if (!props.appId) {
    return;
  }
  ssesubmitting.value = true;
  //创建SSE请求
  const eventSource = new EventSource(
    // todo 手动填写完整的后端地址
    "http://localhost:8101/api/question/ai_generate/sse" +
    `?appId=${props.appId}&optionNumber=${form.optionNumber}&questionNumber${form.questionNumber}`
  );
  let first = true;
  //接收消息
  eventSource.onmessage = function (event) {
    if (first) {
      props.onSSEStart?.(event);
      handleCancel();
      first = !first;
    }
    console.log(event.data)
    props.onSSESuccess?.(JSON.parse(event.data));
  }
  //报错或连接关闭时触发
  eventSource.onerror = function (event) {
    if (event.eventPhase === EventSource.CLOSED) {
      console.log("连接已关闭");
      props.onSSEClose?.(event);
      eventSource.close();
    }
  }

  ssesubmitting.value = false;
}
</script>

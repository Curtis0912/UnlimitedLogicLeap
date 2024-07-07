<template>
  <div id="addQuestionPage">
    <h2 class="title">设置题目</h2>
    <a-form :model="questionContent"
            style=" width: 1200px; margin:0 auto "
            @submit="handleSubmit"
            auto-label-width
            label-align="left">
      <a-form-item label="应用id">
        {{ appId }}
      </a-form-item>
      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-space size="large">
          <a-button @click="addQuestion(questionContent.length)">底部添加题目</a-button>
          <AIGenerateQuestionModel :appId="appId" :onSuccess="aiGenerateQuestion"/>
        </a-space>
        <!--        遍历每道题目-->
        <div v-for="(question,index) in questionContent" :key="index">
          <a-space size="large">
            <h3>题目 {{ index + 1 }}</h3>
            <a-button @click="addQuestion(index+1)" size="small">添加题目</a-button>
            <a-button @click="deleteQuestion(index)" size="small" status="danger">删除题目</a-button>
          </a-space>
          <a-form-item field="posts.post1" :label="`题目 ${index + 1} 标题`">
            <a-input v-model="question.title" placeholder="请输入标题" />
          </a-form-item>
          <!--题目选项开始-->
          <a-space size="large">
            <h4>题目 {{ index + 1 }} 选项列表</h4>
            <a-button @click="addQuestionOption(question,question.options.length)" size="small">底部添加选项</a-button>
          </a-space>
          <a-form-item v-for="(option,optionIndex) in question.options" :key="optionIndex" :content-flex="false"
                       :merge-props="false" :label="`选项 ${optionIndex + 1}`">
            <a-space size="large">
              <a-form-item label="选项key">
                <a-input v-model="option.key" placeholder="请输入选项key" />
              </a-form-item>
              <a-form-item label="选项value">
                <a-input v-model="option.value" placeholder="请输入选项value" />
              </a-form-item>
              <a-form-item label="选项结果">
                <a-input v-model="option.result" placeholder="请输入选项结果" />
              </a-form-item>
              <a-form-item label="选项得分">
                <a-input v-model="option.score" placeholder="请输入选项得分" />
              </a-form-item>
            </a-space>

            <a-space size="large">
              <a-button @click="addQuestionOption(question,optionIndex+1)" size="mini">添加选项</a-button>
              <a-button @click="deleteQuestionOption(question,optionIndex)" size="mini" status="danger">删除选项</a-button>
            </a-space>
          </a-form-item>
          <!--题目选项结束-->
        </div>

      </a-form-item>
      <a-form-item>
        <a-button type="primary" style="width: 120px;margin-left: 70px" html-type="submit">提交</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import API from "@/api";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { addAppUsingPost, editAppUsingPost, getAppVoByIdUsingGet } from "@/api/appController";
import AIGenerateQuestionModel from "@/views/add/components/AIGenerateQuestionModel.vue";
import { withDefaults, defineProps } from "vue";
import { addQuestionUsingPost, editQuestionUsingPost, listQuestionVoByPageUsingPost } from "@/api/questionController";

interface Props {
  appId: string,
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  }
});

//题目内容结构（题目列表）
const questionContent = ref<API.QuestionContentDTO[]>([]);

//添加题目
const addQuestion = (index: number) => {
  questionContent.value.splice(index, 0, {
    title: "",
    options: []
  });
};

//删除题目
const deleteQuestion = (index: number) => {
  questionContent.value.splice(index, 1);
};

//添加题目选项
const addQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: ""
  });
};

//删除题目选项
const deleteQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

const oldQuestion = ref<API.QuestionVO>();

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",//按创建时间排序
    sortOrder: "descend"//降序排序
  });
  if (res.data.code === 0 && res.data.data?.records) {
    oldQuestion.value = res.data.data?.records[0];//把第一条数据给oldQuestion
    if (oldQuestion.value) {//如果有旧数据则加载旧数据，没有则加载个空列表
      questionContent.value = oldQuestion.value.questionContent ?? [];
    }
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
//获取旧数据
watchEffect(() => {
  loadData();
});

const router = useRouter();
const loginUserStore = useLoginUserStore();
/**
 * 提交表单
 * @param data
 */
const handleSubmit = async () => {
  if (!props.appId || !questionContent.value) {
    return;
  }
  let res : any;
  //判断是编辑还是新增
  if (oldQuestion.value?.id) {
    res = await editQuestionUsingPost({
      id: oldQuestion.value.id,
      questionContent: questionContent.value,
    });
  } else {
    res = await addQuestionUsingPost({
      appId: props.appId as any,
      questionContent: questionContent.value,
    });
  }
  if (res.data.code === 0) {
    message.success("操作成功，跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    },2000)
  } else {
    message.error("操作失败," + res.data.message);
  }
};

/**
 * AI生成题目成功后的处理
 * @param result
 */
const aiGenerateQuestion = (result: API.QuestionContentDTO[]) => {
  message.success(`AI生成题目成功，生成 ${result.length} 道题目`);
  questionContent.value = [...questionContent.value,...result];
}

</script>

<style scoped>
#addQuestionPage {

}

.title {
  text-align: center;
  margin-bottom: 32px;
}
</style>

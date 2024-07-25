<template>
  <div id="addAppPage">
    <h2 class="title">创建应用</h2>
    <a-form :model="form"
            style=" width: 400px; margin:0 auto "
            @submit="handleSubmit"
            auto-label-width
            label-align="left">
      <a-form-item field="appName" label="应用名称">
        <a-input v-model="form.appName" placeholder="请输入应用名称" />
      </a-form-item>
      <a-form-item field="appDesc" label="应用描述">
        <a-input v-model="form.appDesc" placeholder="请输入应用描述" />
      </a-form-item>
<!--      <a-form-item field="appIcon" label="应用图标">-->
<!--        <a-input v-model="form.appIcon" placeholder="请输入应用图标地址" />-->
<!--      </a-form-item>-->
      <a-form-item field="appIcon" label="应用图标">
        <PictureUploader biz="app-icon" :value="form.appIcon" :onChange="(value) => (form.appIcon = value)" />
      </a-form-item>
      <a-form-item field="appType" label="应用类型">
        <a-select v-model="form.appType" :style="{width:'330px'}" placeholder="请输入应用类型">
          <a-option v-for="(value,key) of APP_TYPE_MAP" :value="Number(key)" :label="value" />
        </a-select>
      </a-form-item>
      <a-form-item field="scoringStrategy" label="评分策略">
        <a-select v-model="form.scoringStrategy" :style="{width:'330px'}" placeholder="请输入评分策略">
          <a-option v-for="(value,key) of APP_SCORING_STRATEGY_MAP" :value="Number(key)" :label="value" />
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" style="width: 120px;margin-left: 70px" html-type="submit">提交</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import API from "@/api";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { addAppUsingPost, editAppUsingPost, getAppVoByIdUsingGet } from "@/api/appController";
import { APP_TYPE_MAP, APP_SCORING_STRATEGY_MAP } from '@/constant/app';
import PictureUploader from "@/components/PictureUploader.vue";
import { withDefaults, defineProps } from "vue";

interface Props {
  id: string,
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
  appName: "",
  appDesc: "",
  appIcon: "",
  appType: 0,
  scoringStrategy: 0
} as API.AppAddRequest);

const oldApp = ref<API.AppVO>();

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
    oldApp.value = res.data.data;
    console.log(oldApp.value);
    form.value = { ...form.value, ...oldApp.value };
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
  let res : any;
  //判断是编辑还是新增  有id则是编辑
  if (props.id) {
    res = await editAppUsingPost({
      id: props.id as any,
      ...form.value
    });
  } else {
    res = await addAppUsingPost(form.value);
  }
  if (res.data.code === 0) {
    message.success("操作成功，请等待审核");
    setTimeout(() => {
      router.push(`/app/detail/${props.id || res.data.data}`);
    },2000)
  } else {
    message.error("操作失败," + res.data.message);
  }
};

</script>

<style scoped>
#addAppPage {

}

.title {
  text-align: center;
  margin-bottom: 32px;
}
</style>

<template>
  <div id="addScoringResultPage">
    <h2 class="title">设置评分结果</h2>
    <a-form :model="form"
            style=" width: 400px; margin:0 auto "
            @submit="handleSubmit"
            auto-label-width
            label-align="left">
      <a-form-item label="应用 id">{{ appId }}</a-form-item>
      <a-form-item v-if="updateId" label="修改评分 id">{{ updateId }}</a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>
      <a-form-item field="resultPicture" label="结果图标">
        <a-input v-model="form.resultPicture" placeholder="请输入结果图标地址" />
      </a-form-item>
      <!--      <a-form-item field="resultPicture" label="结果图标">-->
      <!--        <PictureUploader :value="form.resultPicture" :onChange="(value) => (form.resultPicture = value)" />-->
      <!--      </a-form-item>-->
      <a-form-item field="resultProp" label="结果集">
        <a-input-tag v-model="form.resultProp" :style="{ width: '320px'}" placeholder="请输入结果集,按回车确认" allow-clear />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number v-model="form.resultScoreRange" placeholder="请输入结果得分范围" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" style="width: 120px;margin-left: 70px" html-type="submit">提交</a-button>
      </a-form-item>
    </a-form>
    <h2 class="title">评分结果管理</h2>
    <ScoringResultTable :appId="appId" :doUpdate="doUpdate" ref="tableRef"/>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import API from "@/api";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { addScoringResultUsingPost, editScoringResultUsingPost, getScoringResultVoByIdUsingGet } from "@/api/scoringResultController";
import { APP_TYPE_MAP, APP_SCORING_STRATEGY_MAP } from '@/constant/app';
import PictureUploader from "@/components/PictureUploader.vue";
import ScoringResultTable from "@/views/add/components/ScoringResultTable.vue";
import { withDefaults, defineProps } from "vue";

interface Props {
  appId: string,
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  }
});

/**
 * 表单信息
 */
const form = ref({
  resultName: "",
  resultDesc: "",
  resultPicture: "",
} as API.ScoringResultAddRequest);

//用于判断是否更新
let updateId = ref();

/**
 * 修改
 */
const doUpdate = (scoringResult: API.ScoringResultVo) => {
  updateId.value = scoringResult.id,
    form.value = scoringResult;
}

//获取旧数据
watchEffect(() => {
  // loadData();
});

const router = useRouter();
const tableRef = ref();
/**
 * 提交表单
 * @param data
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  let res : any;
  //判断是编辑还是新增  有id则是编辑
  if (updateId.value) {
    res = await editScoringResultUsingPost({
      id: updateId.value as any,
      ...form.value
    });
  } else {
    res = await addScoringResultUsingPost({
      //创建需要带上appId
      appId: props.appId as any,
      ...form.value,
    });
  }
  if (res.data.code === 0) {
    message.success("操作成功");
  } else {
    message.error("操作失败," + res.data.message);
  }
  if (tableRef.value) {
    tableRef.value.loadData();
    updateId.value = undefined;
  }
};

</script>

<style scoped>
#addScoringResultPage {

}

.title {
  text-align: center;
  margin-bottom: 32px;
}
</style>

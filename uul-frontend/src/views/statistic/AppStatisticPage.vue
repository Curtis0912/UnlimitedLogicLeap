<template>
  <div id="appStatisticPage">
    <h2>热门应用统计</h2>
    <v-chart :option="appAnswerCountOptions" style="height: 300px;"/>
    <h2>应用结果统计</h2>
    <div class="searchBar">
      <a-input-search
        :style="{ width: '320px'}"
        placeholder="输入 appId"
        button-text="搜索"
        search-button
        @search="(value) => loadAppAnswerResultCountData(value)"
      />
    </div>

    <v-chart :option="appAnswerResultCountOptions" style="height: 300px;"/>
    <div style="margin-bottom: 16px;"/>

    <h2>应用类型统计</h2>
    <v-chart :option="appCountOptions" style="height: 300px;"/>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message";
import API from "@/api";
import {
  getAppAnswerCountUsingGet,
  getAppAnswerResultCountUsingGet,
  getAppCountUsingGet
} from "@/api/appStatisticController";
import VChart from "vue-echarts";
import "echarts";
import { APP_TYPE_MAP } from "@/constant/app";


const appAnswerCountList = ref<API.AppAnswerCountDTO[]>([]);
const appAnswerResultCountList = ref<API.AppAnswerResultCountDTO[]>([]);
const appCountList = ref<API.AppCountDTO[]>([])

/**
 * 加载数据  热门应用
 */
const loadAppAnswerCountData = async () => {
  const res = await getAppAnswerCountUsingGet();
  if (res.data.code === 0) {
    appAnswerCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const appAnswerCountOptions = computed(() => {
  return {
    xAxis: {
      type: 'category',
      data: appAnswerCountList.value.map((item) => item.appId),
      name: "应用 id"
    },
    yAxis: {
      type: 'value',
      name: "做题用户数"
    },
    series: [
      {
        data: appAnswerCountList.value.map((item) => item.answerCount),
        type: 'bar'
      }
    ]
  }
});


/**
 * 加载数据   应用回答结果统计
 */
const loadAppAnswerResultCountData = async (appId:string) => {
  if (!appId) return;
  const res = await getAppAnswerResultCountUsingGet({
    appId: appId as any,
  });
  if (res.data.code === 0) {
    appAnswerResultCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const appAnswerResultCountOptions = computed(() => {
  return {
    legend: {
      left: 'auto',
      orient: 'vertical'
    },
    toolbox: {
      show: true,
      feature: {
        mark: { show: true },
        dataView: { show: true, readOnly: false },
        restore: { show: true },
        saveAsImage: { show: true }
      }
    },
    series: [
      {
        name: 'Nightingale Chart',
        type: 'pie',
        radius: [50, 120],
        center: ['50%', '50%'],
        roseType: 'area',
        itemStyle: {
          borderRadius: 8
        },
        data: appAnswerResultCountList.value.map((item) => {
          return {
            value: item.resultCount,
            name: item.resultName,
          }
        }),
      }
    ]
  }
})


/**
 * 加载数据   应用类型统计
 */
const loadAppCountData = async () => {
  const res = await getAppCountUsingGet();
  if (res.data.code === 0) {
    appCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const appCountOptions = computed(() => {
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '应用类型统计',
        type: 'pie',
        radius: '80%',
        data: appCountList.value.map((item) => {
          return {
            value: item.appCount,
            name: APP_TYPE_MAP[item.appType],
          }
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})


/**
 * 改变时触发数据的重新加载
 */
watchEffect(() => {
  loadAppAnswerCountData();
  loadAppAnswerResultCountData("");
  loadAppCountData();
});

</script>

<style>
.searchBar {
  text-align: left;
}
</style>

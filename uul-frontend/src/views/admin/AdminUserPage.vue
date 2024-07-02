<template>
  <a-form :model="formSearchParams"
          :style="{marginBottom: '20px' }"
          layout="inline"
          @submit="doSearch">
    <a-form-item field="userName" label="用户名">
      <a-input allow-clear v-model="formSearchParams.userName" placeholder="please enter your userName" />
    </a-form-item>
    <a-form-item field="userAccount" label="账号">
      <a-input allow-clear v-model="formSearchParams.userAccount" placeholder="please enter your userAccount" />
    </a-form-item>
    <a-form-item>
      <a-button type="primary" style="width: 120px" html-type="submit">search</a-button>
    </a-form-item>
  </a-form>
  <a-table :columns="columns" :data="dataList"
           :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.current,
      total,
    }"
           @page-change="onPageChange"
  >
    <template #userAvatar="{ record }">
      <a-image width="64" :src="record.userAvatar" />
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-button status="danger" @click="doDelete(record)">delete</a-button>
    </template>
  </a-table>

</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import { deleteUserUsingPost, listUserByPageUsingPost } from "@/api/userController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

const formSearchParams = ref<API.UserQueryRequest>({});

//初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10
};
/**
 * 搜索参数
 */
const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams
});

/**
 * 分页改变时触发,改变搜索条件，触发数据加载
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams,
    current: page
  };
};


/**
 * 定义变量存储获取的数据
 */
const dataList = ref<API.User[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  //获取应用数据
  const res = await listUserByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value
  };
};
/**
 * 删除用户
 */
const doDelete = async (record: API.User) => {
  if (!record.id) {
    return;
  }
  const res = await deleteUserUsingPost({
    id: record.id
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("删除失败," + res.data.message);
  }
};

/**
 * 监听searchParams变量，改变时触发数据的更新加载
 */
watchEffect(() => {
  loadData();
});

const columns = [
  {
    title: "id",
    dataIndex: "id"
  },
  {
    title: "账号",
    dataIndex: "userAccount"
  },
  {
    title: "用户名",
    dataIndex: "userName"
  },
  {
    title: "头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar"
  },
  {
    title: "简介",
    dataIndex: "userProfile"
  },
  {
    title: "角色",
    dataIndex: "userRole"
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime"
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime"
  },
  {
    title: "操作",
    slotName: "optional"
  }
];
const data = reactive([{
  key: "1",
  name: "Jane Doe",
  salary: 23000,
  address: "32 Park Road, London",
  email: "jane.doe@example.com"
}, {
  key: "2",
  name: "Alisa Ross",
  salary: 25000,
  address: "35 Park Road, London",
  email: "alisa.ross@example.com"
}, {
  key: "3",
  name: "Kevin Sandra",
  salary: 22000,
  address: "31 Park Road, London",
  email: "kevin.sandra@example.com"
}, {
  key: "4",
  name: "Ed Hellen",
  salary: 17000,
  address: "42 Park Road, London",
  email: "ed.hellen@example.com"
}, {
  key: "5",
  name: "William Smith",
  salary: 27000,
  address: "62 Park Road, London",
  email: "william.smith@example.com"
}]);


</script>

<template>
  <Editor :value="value" :plugins="plugins" @change="handleChange" />
</template>

<script>
import gfm from '@bytemd/plugin-gfm'
import { Editor, Viewer } from '@bytemd/vue'
import highlight from "@bytemd/plugin-highlight";
import { ref } from "vue";

const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
]

const value = ref("");

const handleChange = (v: string) => {
  value.value = v;
};
</script>

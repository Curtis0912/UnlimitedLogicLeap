<template>
  <Viewer :value="value" :plugins="plugins" />
</template>

<script>
import gfm from '@bytemd/plugin-gfm'
import { Editor, Viewer } from '@bytemd/vue'
import highlight from "@bytemd/plugin-highlight";
import { withDefaults,defineProps } from "vue";


/**
 * 定义组件属性类型
 * @type {BytemdPlugin[]}
 */
interface Props {
  value: string;
}
const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
]

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
})
</script>

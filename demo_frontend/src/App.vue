<script setup>

import {Moon, Sunrise} from "@element-plus/icons-vue";

import {ref} from 'vue'
import {useDark, useToggle} from "@vueuse/core";

useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light',
})

useDark({
  onChanged(dark) {
    useToggle(dark)
  }
})
const openDark = ref(true)

function switchTheme() {
  if (openDark.value) {
    document.documentElement.setAttribute("class", "dark")
  } else {
    document.documentElement.setAttribute("class", "light")
  }
  // openDark.value = !openDark.value
}
</script>

<template>
  <div>
    <div style="position: absolute;right: 20px;top: 20px;z-index: 2">
      <el-switch
          v-model="openDark"
          :active-icon="Sunrise"
          :inactive-icon="Moon"
          class="mt-2"
          inline-prompt
          size="large"
          @click="switchTheme"
      />
    </div>
    <router-view/>
  </div>
</template>

<style scoped>
</style>

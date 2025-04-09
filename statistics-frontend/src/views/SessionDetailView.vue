<script setup>
import {useRouter} from "vue-router";
import { useRoute } from 'vue-router'
import {useSessions} from "@/composables/useSessions.js";
import {onMounted} from "vue";

const {files, sessionsLoading, sessionsError, fetchSession} = useSessions();
const route = useRoute()

onMounted(() =>{
  const id = route.params.id
  fetchSession(id)
})

const router = useRouter()
const goBack = () => {
  router.back() // or router.go(-1)
}
</script>

<template>
<div class="body-container">
  <h2>Session Info</h2>
  <a href="#" @click="goBack">Go back</a>
  <div v-if="files.length > 0" v-for="img in files" :key="img" class="mb-4">
    <img :src="img" alt="Uploaded"  />
  </div>
  <h3 v-else>This session is empty...</h3>
</div>
</template>

<style scoped>
a{
  width: 100%;
  color: blue;
  font-size: 14px;
  padding: 0 8px;
}
</style>

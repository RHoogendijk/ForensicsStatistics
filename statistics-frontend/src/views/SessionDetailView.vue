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

const goReplayView = (files) => {
  router.push({
    name: 'Replay',
    params: { id: route.params.id },
    query: {
      outsideBackgroundURL: files.outsideBackgroundURL,
      basementBackgroundURL: files.basementBackgroundURL,
      jsonFileURL: files.jsonFileURL
    }
  });
}
</script>

<template>
<div class="body-container">
  <h2>Session Info</h2>
  <a href="#" @click="goBack">Go back</a>
  <button @click="goReplayView(files)">Watch Replay</button>
  <div
    v-if="files.photoURLs && files.photoURLs.length > 0"
    v-for="(url, index) in files.photoURLs"
    :key="index"
    class="mb-4"
  >
    <img :src="url" alt="Uploaded" />
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
img{
  width: 80%;
}
button {
  margin: 16px;
}
</style>

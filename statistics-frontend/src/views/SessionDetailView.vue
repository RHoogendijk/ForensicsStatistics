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
  <h3>Replay</h3>
  <button @click="goReplayView(files)">Watch Replay</button>
  <h3>Evidence</h3>
  <div
    v-if="files.evidence && files.evidence.length > 0"
  >
    <p v-for="evidenceString in files.evidence">{{evidenceString}}</p>
  </div>
  <h4 v-else>No evidence collected yet...</h4>
  <h3>Photo's</h3>
  <div class="image-container">
    <div
      v-if="files.photoURLs && files.photoURLs.length > 0"
      v-for="(url, index) in files.photoURLs"
      :key="index"
      class="mb-4"
    >
      <img :src="url" alt="Uploaded" />
    </div>
    <h4 v-else>This session does not have any images...</h4>
  </div>

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
  width: 50vw;
}
button {
  margin: 16px;
}
.image-container{
  scroll-behavior: smooth;
  overflow-y: scroll;
  min-height: 40px;
  max-height: 50vh;
}
h3{
  margin-top: 10px;
}
</style>

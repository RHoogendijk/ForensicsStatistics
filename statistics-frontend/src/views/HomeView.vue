<script setup>
import { useImages } from '@/composables/useImages'

const { images, loading, error } = useImages()
</script>

<template>
<div class="body-container">
  <h2>Home</h2>
    <div>
      <h3>📷 Uploaded Images</h3>
      <div v-if="loading">Loading...</div>
      <div v-else-if="error">Error loading images: {{ error.message }}</div>
      <div v-else>
        <div v-for="img in images" :key="img" class="mb-4">
          <img :src="img" alt="Uploaded"  />
        </div>
      </div>
    </div>
</div>
</template>

<style scoped>
img{
  width: 100%;
}
</style>

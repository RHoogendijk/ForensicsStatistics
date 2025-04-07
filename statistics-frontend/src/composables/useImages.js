import { ref, onMounted } from 'vue'
import { CONFIG } from "@/config.js";

export function useImages() {
  const images = ref([])
  const loading = ref(true)
  const error = ref(null)

  //TODO: fetch based on session id
  const fetchImages = async () => {
    loading.value = true
    error.value = null

    try {
      const res = await fetch(`${CONFIG.BACKEND_URL}/api/files/images`)
      console.log(res)
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`)
      }
      const data = await res.json()
      images.value = data
    } catch (err) {
      console.error('Failed to fetch images:', err)
      error.value = err
    } finally {
      loading.value = false
    }
  }

  onMounted(fetchImages)

  return {
    images,
    loading,
    error,
    fetchImages,
  }
}

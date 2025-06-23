import {ref} from "vue";
import {CONFIG} from "../../config.js";

export function useSessions() {
  const files = ref([]);
  const sessionsLoading = ref(false);
  const sessionsError = ref(null);

  const fetchSession = async (sessionId) => {
    sessionsLoading.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/api/sessions/${sessionId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      if (!response.ok) {
        throw new Error(response.status);
      }
      const data = await response.json();
      files.value = data;
    } catch (error) {
      sessionsError.value = error;
    } finally {
      sessionsLoading.value = false;
    }
  }

  return {files, sessionsLoading, sessionsError, fetchSession};
}

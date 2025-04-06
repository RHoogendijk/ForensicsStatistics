import { ref } from "vue";
import { CONFIG } from "@/config.js";

export function useClasses() {
  const students = ref([]);
  const classes = ref([]);
  const classesErr = ref(null);
  const classesLoading = ref(false);
  const creatingClass = ref(false);

  const fetchClasses = async () => {
    classesLoading.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/classes`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      classes.value = data;
    } catch (error) {
      classesErr.value = error;
    } finally {
      classesLoading.value = false;
    }
  };

  const createClass = async (id) => {
    creatingClass.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/classes`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: id })
      });

      if (!response.ok) throw new Error(response.statusText);

      const newClass = await response.json();
      classes.value.push(newClass);
      return newClass;
    } catch (error) {
      classesErr.value = error;
    } finally {
      creatingClass.value = false;
    }
  };

  const enrollStudent = async (classId, studentId) => {
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/classes/${classId}/enroll/${studentId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`Failed to enroll student: ${response.statusText}`);
      }

      const updatedClass = await response.json();
      return updatedClass;
    } catch (error) {
      classesErr.value = error;
    }
  };

  const fetchStudentsForClass = async (classId) => {
    classesLoading.value = true;
    try {
      const response = await fetch(`${CONFIG.BACKEND_URL}/classes/${classId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch students: ${response.statusText}`);
      }

      const data = await response.json();
      students.value = data;
    } catch (error) {
      classesErr.value = error;
    } finally {
      classesLoading.value = false;
    }
  };

  return {
    students,
    classes,
    classesErr,
    classesLoading,
    fetchClasses,
    creatingClass,
    createClass,
    enrollStudent,
    fetchStudentsForClass
  };
}

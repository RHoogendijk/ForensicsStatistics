<script setup>
import {
  computed,
  onMounted,
  onUnmounted,
  ref,
  watchEffect
} from 'vue'

const time = ref(0)
const max = ref(100)
const isPlaying = ref(false)

let lastFrame = performance.now()

const sliderRef = ref(null)
const showPointer = ref(false)
const isDragging = ref(false)
const mouseX = ref(0)

// Event handlers
function onMouseDown() {
  isDragging.value = true
}

function onMouseUp() {
  isDragging.value = false
}

function onMouseEnter() {
  showPointer.value = true
}

function onMouseLeave() {
  showPointer.value = false
}

function onMouseMove(e) {
  if (!sliderRef.value) return

  const slider = sliderRef.value
  const rect = slider.getBoundingClientRect()

  // Get the mouse position within the slider bounds
  const boundedX = Math.min(Math.max(e.clientX - rect.left, 0), rect.width)

  // Update the mouseX value and time based on the position
  mouseX.value = boundedX
}

function togglePlay() {
  isPlaying.value = !isPlaying.value
}

// Main animation loop
function update(now) {
  const deltaTime = (now - lastFrame) / 1000
  lastFrame = now

  if (isPlaying.value && !isDragging.value && time.value < max.value) {
    time.value = Math.min(time.value + deltaTime, max.value)
  }

  requestAnimationFrame(update)
}

// Lifecycle hooks
onMounted(() => {
  lastFrame = performance.now()
  requestAnimationFrame(update)

  window.addEventListener('mouseup', onMouseUp)
  window.addEventListener('mousemove', onMouseMove)
})

onUnmounted(() => {
  window.removeEventListener('mouseup', onMouseUp)
  window.removeEventListener('mousemove', onMouseMove)
})

// Computed values
const formattedTime = computed(() => {
  let totalSeconds = Math.round(time.value)
  const minutes = Math.floor(time.value / 60)
  const seconds = totalSeconds % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

const sliderStyle = computed(() => {
  const percentage = (time.value / max.value) * 99
  return {
    background: `linear-gradient(to right, #FDBA69 0%, #FDBA69 ${percentage}%, #F6F7F8 ${percentage}%, #F6F7F8 100%)`
  }
})

const pointerStyle = computed(() => {
  if (!sliderRef.value) return {}
  const slider = sliderRef.value
  const rect = slider.getBoundingClientRect()
  const x = mouseX.value
  return { left: `${x}px` }
})

const hoveredTime = computed(() => {
  if (!sliderRef.value) return 0
  const rect = sliderRef.value.getBoundingClientRect()
  const percent = Math.min(Math.max((mouseX.value) / rect.width, 0), 1)
  return percent * max.value
})

const formattedPointerTime = computed(() => {
  const totalSeconds = Math.round(isDragging.value ? time.value : hoveredTime.value)
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// Reposition pointer on time update
watchEffect(() => {
  if (!sliderRef.value) return
  const slider = sliderRef.value
  const rect = slider.getBoundingClientRect()
  const percent = time.value / max.value
  showPointer.value = percent * rect.width
})
</script>

<template>
  <div class="body-container">
    <h2>Replay</h2>
    <div class="slider-container">
      <label for="time">Time: {{ formattedTime }}</label>
      <div class="slider-wrapper" >
      <input
        ref="sliderRef"
        type="range"
        id="time"
        min="0"
        step="0.1"
        :max="max"
        v-model.number="time"
        :style="sliderStyle"
        @mousedown="onMouseDown"
        @mouseenter="onMouseEnter"
        @mouseleave="onMouseLeave"
      />
        <transition name="pointer-fade">
          <div
            v-if="showPointer"
            class="thumb-pointer"
            :style="pointerStyle"
          >
            {{ formattedPointerTime }}
          </div>
        </transition>
        </div>
      <button @click="togglePlay">{{ isPlaying ? 'Pause' : 'Play' }}</button>
    </div>
  </div>
</template>

<style scoped>
.slider-container{
  width: 75%;
}
input[type="range"] {
  -webkit-appearance: none;
  width: 100%;
  height: 8px;
  border-radius: 4px;
  cursor: pointer;
  outline: 1px solid #ccc;
  padding: 4px 0;
}

/* Track — minimal styling, overridden by inline style */
input[type="range"]::-webkit-slider-runnable-track {
  height: 8px;
  border-radius: 4px;
}

/* Thumb */
input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  height: 8px;
  width: 16px;
  background: #FDBA69;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

input[type="range"]::-moz-range-thumb {
  height: 8px;
  width: 16px;
  background: #FDBA69;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

/* Firefox track (uses different pseudo-element) */
input[type="range"]::-moz-range-track {
  height: 8px;
  border-radius: 4px;
}

.slider-wrapper {
  position: relative;
  width: 100%;
}

.thumb-pointer {
  position: absolute;
  bottom: 100%; /* places it above the slider */
  transform: translateX(-50%);

  background-color: #FDBA69;
  border-radius: 25%;
  margin-bottom: -2px;
  margin-left: 0.5rem;
  width: 2rem;
  text-align: center;
  pointer-events: none;
  font-size: 0.75rem;
  padding: 0.2rem;
}

.pointer-fade-enter-active,
.pointer-fade-leave-active {
  transition: opacity 0.2s ease;
}
.pointer-fade-enter-from,
.pointer-fade-leave-to {
  opacity: 0;
}
.pointer-fade-enter-to,
.pointer-fade-leave-from {
  opacity: 1;
}
</style>

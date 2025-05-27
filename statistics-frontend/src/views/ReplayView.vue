<script setup>
import {
  computed, nextTick,
  onMounted,
  onUnmounted,
  ref, watch,
  watchEffect
} from 'vue'

const data = {
  duration: 5.0,
  positions: [
    {time: 0.0, x: 0.0, y: 0.0, angle: 0.0, floor: 0},
    {time: 0.5, x: 10.0, y: 5.0, angle: 0.0, floor: 0},
    {time: 1.0, x: 25.0, y: 15.0, angle: 0.0, floor: 0},
    {time: 1.5, x: 35.0, y: 40.0, angle: 0.0, floor: 0},
    {time: 2.0, x: 45.0, y: 25.0, angle: 0.0, floor: 0},
    {time: 2.5, x: 60.0, y: 35.0, angle: 0.0, floor: 0},
    {time: 3.0, x: 65.0, y: 60.0, angle: 0.0, floor: 0},
    {time: 3.5, x: 75.0, y: 55.0, angle: 0.0, floor: 0},
    {time: 4.0, x: 85.0, y: 75.0, angle: 0.0, floor: 0},
    {time: 4.5, x: 90.0, y: 85.0, angle: 0.0, floor: 0},
    {time: 5.0, x: 100.0, y: 100.0, angle: 0.0, floor: 0},
  ],
  logs: [
    {time: 1.2, message: "User picked up camera"},
    {time: 2.0, message: "User took a picture of Spectrometer"},
    {time: 3.2, message: "User took a picture of IBC Tank"},
    {time: 4.2, message: "User scanned substance containing Piperonylketon, water"}
  ],
  events: [
    {
      time: 2.0,
      type: "taken_photo",
    },
    {
      time: 3.2,
      type: "taken_photo",
    }
  ]
};

const time = ref(0)
const max = computed(() => data.duration)
const positions = computed(() => data.positions)
const logs = computed(() => data.logs)
const visibleLogs = computed(() => {
  return logs.value.filter(log => log.time < time.value)
})
const isPlaying = ref(false)

let lastFrame = performance.now()

const sliderRef = ref(null)
const showPointer = ref(false)
const isDragging = ref(false)
const mouseX = ref(0)

const bgImage = new Image()
bgImage.src = new URL('@/assets/img/replay/bg/stable-bg.png', import.meta.url).href

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

//canvas functions
function drawFrame() {
  ctx.clearRect(0, 0, 854, 480)
  drawBackground()
  drawPlayer()
}

function drawBackground() {
  if (!bgImage.complete) return
  ctx.drawImage(bgImage, 0, 0, 854, 480)
}

function drawPlayer() {
  const pos = getPlayerPosAtCurrentTime(time.value)
  if (!ctx || !pos) return

//Draw a circle at the player position
  ctx.fillStyle = '#FDBA69'
  ctx.beginPath()
  ctx.arc(pos.x, pos.y, 8, 0, Math.PI * 2)
  ctx.fill()
}


function getPlayerPosAtCurrentTime(currentTime) {
  const pos = positions.value
  if (!pos || pos.length === 0) return null

  // Clamp edges
  if (currentTime <= pos[0].time) return scaleToCanvas(pos[0])
  if (currentTime >= pos[pos.length - 1].time) return scaleToCanvas(pos[pos.length - 1])

  // Find surrounding positions
  for (let i = 0; i < pos.length - 1; i++) {
    const p1 = pos[i]
    const p2 = pos[i + 1]

    if (currentTime >= p1.time && currentTime <= p2.time) {
      const t = (currentTime - p1.time) / (p2.time - p1.time)

      const interpolated = {
        x: p1.x + (p2.x - p1.x) * t,
        y: p1.y + (p2.y - p1.y) * t,
        angle: p1.angle + (p2.angle - p1.angle) * t,
        floor: p1.floor
      }

      return scaleToCanvas(interpolated)
    }
  }

  return null
}

function scaleToCanvas(position) {
  const canvasWidth = 854
  const canvasHeight = 480

  return {
    x: position.x / 100 * canvasWidth,  // assuming 100 is max x
    y: position.y / 100 * canvasHeight, // assuming 100 is max y
    angle: position.angle,
    floor: position.floor
  }
}

const cRef = ref(null)
let ctx;

// Lifecycle hooks
onMounted(() => {
  //playbar
  lastFrame = performance.now()
  requestAnimationFrame(update)

  window.addEventListener('mouseup', onMouseUp)
  window.addEventListener('mousemove', onMouseMove)

  //canvas
  const c = cRef.value
  ctx = c.getContext('2d')
  c.width = 854
  c.height = 480
  ctx.beginPath()
  ctx.rect(20, 20, 150, 100)
  ctx.stroke()
  console.log(positions.value)
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
  return {left: `${x}px`}
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
  if (!sliderRef.value || !ctx) return
  //update playbar ui
  const slider = sliderRef.value
  const rect = slider.getBoundingClientRect()
  const percent = time.value / max.value
  showPointer.value = percent * rect.width

  //update canvas
  drawFrame()
})

const logContainer = ref(null)

watch(visibleLogs, () => {
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
})
</script>

<template>
  <div class="body-container">
    <h2>Replay</h2>
    <canvas ref="cRef"></canvas>
    <div class="slider-container">
      <label for="time">Time: {{ formattedTime }}</label>
      <div class="slider-wrapper">
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
    <div class="logs-container">
      <h3>Logs</h3>
      <div class="scrollable" ref="logContainer">
        <p v-for="log in visibleLogs" :key="log.time">{{ log.time }}s | {{ log.message }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
canvas {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  border: 1px solid #ccc
}

.slider-container {
  width: 75%;
}
.scrollable {
  scroll-behavior: smooth;
  overflow-y: scroll;
  height: 40px;
}
.logs-container {
  width: 75%;
  padding: 8px;
  border-radius: 8px;
  background-color: #cccccc;
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

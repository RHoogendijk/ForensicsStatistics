<script setup>
import {
  computed, nextTick,
  onMounted,
  onUnmounted,
  ref, watch,
  watchEffect
} from 'vue'
import {useRouter} from "vue-router";

const props = defineProps([
  'id',
  'outsideBackgroundURL',
  'basementBackgroundURL',
  'jsonFileURL'
])

const data = ref(null)

const time = ref(0)
const positions = computed(() => data.value?.positions || [])
const events = computed(() => data.value?.events || [])
const logs = computed(() => data.value?.logs || [])
const max = computed(() => data.value?.duration || 0)
const visibleLogs = computed(() => {
  return logs.value.filter(log => log.time < time.value)
})
const isPlaying = ref(false)

let lastFrame = performance.now()
const playbackSpeed = ref(1);
const speeds = [0.5, 1, 2, 4,8]

const sliderRef = ref(null)
const showPointer = ref(false)
const isDragging = ref(false)
const mouseX = ref(0)

const bgImage = new Image()

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
  const deltaTime = ((now - lastFrame) / 1000) * playbackSpeed.value
  lastFrame = now

  if (isPlaying.value && !isDragging.value && time.value < max.value) {
    time.value = Math.min(time.value + deltaTime, max.value)
  }

  requestAnimationFrame(update)
}

//canvas functions
function getLatestPositionBeforeCurrentTime() {
  if (!positions.value.length) return null;

  // Filter positions with time <= current time
  const filtered = positions.value.filter(pos => pos.time <= time.value);

  // Return the last one, or null if none found
  return filtered.length ? filtered[filtered.length - 1] : null;
}

function drawFrame() {
  if (!data.value || positions.value.length === 0) return // ✅ skip if no data
  ctx.clearRect(0, 0, 854, 480)
  drawBackground()
  drawEvent()
  drawPlayer()
}

function drawBackground() {
  const floor = getLatestPositionBeforeCurrentTime().floor
  if (floor === 0){
    bgImage.src = props.outsideBackgroundURL;
  } else {
    bgImage.src = props.basementBackgroundURL;
  }
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

  ctx.fillStyle = '#0053ff'
  ctx.beginPath()
  ctx.arc(pos.x, pos.y, 8, (pos.angle -60)* (Math.PI / 180), (pos.angle + 60) * (Math.PI / 180))
  ctx.fill()
}

function drawEvent(){
 const lastEvent = getLatestEventBeforeCurrentTime();
  if (!lastEvent) {
    return
  }
  const eventTime = lastEvent.time
  const currentTime = time.value
  const timeSinceEvent = currentTime - eventTime

  if (timeSinceEvent > 2) {
    return
  }

  // draw event
  const pos = getPlayerPosAtCurrentTime(eventTime)

  if (!ctx || !pos) return

  const size = 50; // size of the triangle
  const angleRad = pos.angle * (Math.PI / 180); // convert angle to radians, rotated so tip points correctly

  // Tip of the triangle (pointing direction)
  const tipX = pos.x;
  const tipY = pos.y;

  // Calculate base points (behind the tip)
  const baseAngle1 = angleRad + Math.PI / 6; // 30 degrees offset
  const baseAngle2 = angleRad - Math.PI / 6; // 30 degrees offset

  const baseX1 = tipX + size * Math.cos(baseAngle1);
  const baseY1 = tipY + size * Math.sin(baseAngle1);

  const baseX2 = tipX + size * Math.cos(baseAngle2);
  const baseY2 = tipY + size * Math.sin(baseAngle2);

  ctx.fillStyle = 'rgba(255,225,163,0.6)';
  ctx.beginPath();
  ctx.moveTo(tipX, tipY);
  ctx.lineTo(baseX1, baseY1);
  ctx.lineTo(baseX2, baseY2);
  ctx.closePath();
  ctx.fill();
}

function getLatestEventBeforeCurrentTime() {
  if (!events.value.length) return null;

  // Filter positions with time <= current time
  const filtered = events.value.filter(e => e.time <= time.value);

  // Return the last one, or null if none found
  return filtered.length ? filtered[filtered.length - 1] : null;
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
onMounted(async () => {
  bgImage.src = props.outsideBackgroundURL;
  const response = await fetch(props.jsonFileURL)
  data.value = await response.json()

  await nextTick() // wait for DOM refs to be ready

  // Setup canvas
  const c = cRef.value
  ctx = c.getContext('2d')
  c.width = 854
  c.height = 480

  drawFrame() // ✅ draw at least once when data is ready

  // Start animation
  lastFrame = performance.now()
  requestAnimationFrame(update)

  // Mouse events
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

function formatTime(time) {
  let totalSeconds = Math.round(time)
  const minutes = Math.floor(time / 60)
  const seconds = totalSeconds % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

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
watch(time, () => {
  if (!sliderRef.value || !ctx) return

  const slider = sliderRef.value
  const rect = slider.getBoundingClientRect()
  const percent = time.value / max.value
  showPointer.value = percent * rect.width

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

const router = useRouter()
const goBack = () => {
  router.back() // or router.go(-1)
}
</script>

<template>
  <div class="body-container">
    <h2>Replay</h2>
    <a href="#" @click="goBack">Go back</a>
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
    </div>
    <div>
      <button @click="togglePlay" :class="{'play': !isPlaying}">
        <img :src="isPlaying ? '/src/assets/img/replay/pause.png' : '/src/assets/img/replay/play.png'" alt="Play/Pause Icon" width="32px" height="32px" />
      </button>
    </div>
    <div>
      <button
        v-for="speed in speeds"
        :key="speed"
        @click="playbackSpeed = speed"
        :class="{
        'highlighted': playbackSpeed === speed,
      }"
        class="speed"
      >
        {{ speed }}x
      </button>
    </div>
    <div class="logs-container">
      <h3>Logs</h3>
      <div class="scrollable" ref="logContainer">
        <p v-for="log in visibleLogs" :key="log.time">{{ formatTime(log.time) }} | {{ log.message }}</p>
      </div>
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
button{
  width: 54px;
  height: 54px;
  border-radius: 50%;
}
button.speed{
  margin-left: 1px;
  margin-right: 1px;
  height: 32px;
  border-radius: 2px;
  border: 1px solid rgba(200, 200, 203, 0.5);
  margin-bottom: 8px;
}
button.play{
  width: 54px;
  height: 54px;
  border-radius: 50%;
  padding-left: 12px;
  padding-top: 10px;
}
.highlighted{
  background-color: #FDBA69;
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

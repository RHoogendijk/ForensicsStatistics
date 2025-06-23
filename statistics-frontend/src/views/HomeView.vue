<script setup>

</script>

<template>
<div class="body-container">
    <h2>Basic Controls</h2>
    <p>To begin your investigation, it's important to understand the basic VR controls. You'll be using one controller in each hand.</p>
    <ul>
      <li><b>Left Controller:</b> This is used to move your character around. Use the joystick with your left thumb to walk or navigate through the environment.</li>
      <li><b>Right Controller:</b> This is primarily for looking around. Use the joystick with your right thumb to rotate your view.</li>
    </ul>
    <p>In addition to using the controllers, you can also look around naturally by turning your head, just like in real life.</p>
  <div class="img-container">
    <img src="../assets/img/tutorial-1.png" alt="">
    <img src="../assets/img/tutorial-2.png" alt="">
  </div>
  <h2>Making use of the website</h2>
    <p>When starting a investigation, the game will ask you for your upload code. You can find your upload code by signing in to the website and going to your profile. The game needs this upload code in order to link your playsession to the correct user. When you're done with the game, make sure to press the "Exit Investigation" button in the starting tent to make sure everything gets saved properly.
      <br> <br>Finally, you can view your taken photo's, collected evidence and replay of all your played sessions in the "Session" tab on this website.</p>
 </div>
</template>

<style scoped>
img{
  width: 100%;
}
.body-container {
  padding: 32px 10vw;
}
ul{
  margin-left: 0;
  padding-left: 1em;
  width: 100%;

}
.img-container {
  display: flex;
  flex-direction: row;
  width: 100%;
  align-items: center;
  justify-content: center;
}
.img-container img{
  width: 15vw;
  aspect-ratio: auto;
}

p{
  text-align: left;
  width: 100%;
}
</style>

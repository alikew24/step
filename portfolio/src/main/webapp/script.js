// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var slideIndex = 0;

function createDots() {
  var slides = document.getElementsByClassName("individual-slide");
  const dotContainer = document.getElementById('dot-container');
  for (var i = 0; i < slides.length; i++) {
    var span = document.createElement("SPAN");
    span.className = "dot";
    dotContainer.appendChild(span);
  }
}

function showNextSlide() {
  var slides = document.getElementsByClassName("individual-slide");
  var dots = document.getElementsByClassName("dot");
  for (var i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";  
  }
  slideIndex++;
  if (slideIndex > slides.length) {
      slideIndex = 1
   }    
  for (i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block";  
  dots[slideIndex-1].className += " active";
  setTimeout(showNextSlide, 2000); // Change image every 2 seconds
}

function getComments() {
  let maxComments = 4;
  if (document.getElementById("maxComments") && document.getElementById("maxComments").value) {
    maxComments = document.getElementById("maxComments").value;
  }
  fetch('/data?numComments=' + maxComments).then(response => response.json()).then((comments) => { 
    const commentsListElement = document.getElementById('comments-container');
    commentsListElement.innerHTML = '';
    for (var i = 0; i < comments.length; i++) {
      commentsListElement.appendChild(
      createListElement(comments[i]));
    }
  });
}

function deleteComments() {
  fetch('/delete-data', {method: 'POST'}).then((response) => {
      getComments();
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];
  const hobbies = 
      ['Running', 'Hiking', 'Rock Climbing', 'Photography', 'Traveling'];
  const links =
      ['Projects', 'Hobbies'];

  // Pick a random link.
  const link = links[Math.floor(Math.random() * links.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  if (link == 'Hobbies'){
      greetingContainer.innerHTML = "<a href='hobbies.html'> Hobbies </a>";
  }
  else {
      greetingContainer.innerHTML = "<a href='projects.html'> Projects </a>";
  }
}


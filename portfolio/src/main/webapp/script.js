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
      createListElement(comments[i])).className = "speech-bubble arrow bottom";
    }
  });
}

function bodyOnload() {
  showOrHideComments()
  getComments();
  navBarLogin();
}

function navBarLogin() {
  fetch('/login').then(response => response.json()).then((loginStatus) => {
    navBar = document.getElementById("navigation");
    const liElement = document.createElement('li');
    if (loginStatus.isLoggedIn == true) {
      liElement.innerHTML = "<a href='/loginlogout'>Logout</a>";
      navBar.appendChild(liElement);
    }
    else {
      liElement.innerHTML = "<a href='/loginlogout'>Login</a>";
      navBar.appendChild(liElement);
    }     
  });  
}

function mapPageOnload() {
    navBarLogin();
    createMap();
}

function showOrHideComments() {
  fetch('/login').then(response => response.json()).then((loginStatus) => {
    console.log(loginStatus);
    const comments = document.getElementById("login-accessible-comment-features");
    const loginButton = document.getElementById("login-form");
    if (loginStatus.isLoggedIn == true) {
      comments.className = "show-comments";
      loginButton.className = "hide-login-form";
    }
    else {
      loginButton.className = "show-login-form";
      comments.className = "hide-comments";
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

/** Creates a map and adds it to the page. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 40.710101, lng: -74.355919}, zoom: 16,
      styles: [
            {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
            {
              featureType: 'administrative.locality',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{color: '#263c3f'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'labels.text.fill',
              stylers: [{color: '#6b9a76'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{color: '#38414e'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{color: '#212a37'}]
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{color: '#9ca5b3'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{color: '#746855'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{color: '#1f2835'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{color: '#f3d19c'}]
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{color: '#2f3948'}]
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{color: '#17263c'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{color: '#515c6d'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{color: '#17263c'}]
            }
          ]
        });
  addLandmark(map, 40.712741, -74.353634, 'Overlook Hospital', 'This is Overlook Hospital, where I was born! Fun fact: my mom walked to the hospital when she was in labor with me!');
  addLandmark(map, 40.710144, -74.355775, 'My house', 'This is my house, where I have lived my entire life! We have had it for over 25 years.');
  addLandmark(map, 40.720444, -74.371156, 'Summit High School', 'This is the highschool I attended. Summit High is home to a lot of my favorite memories, where I met my best friends, and where I took my first coding class!');
  addLandmark(map, 40.715993, -74.360045, 'Summit YMCA', 'This is the YMCA, where I had swim practices from second grade to my senior year of high school! I was on a club team called the Summit Seals, and swam competitively for over 10 years');
  addLandmark(map, 40.707735, -74.365906, 'Memorial Field', 'This is memorial field, a park right by my house. This is where I learnt how to ride a bike, hung out with my friends, and played tennis in high school');
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title, animation: google.maps.Animation.DROP});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
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

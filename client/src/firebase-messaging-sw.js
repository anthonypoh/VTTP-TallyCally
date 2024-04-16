importScripts('https://www.gstatic.com/firebasejs/9.6.4/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.6.4/firebase-messaging-compat.js');

const firebaseConfig = {
  apiKey: "AIzaSyDGY3MANULeNPjGYJoYF-hU91jqfGd8oA8",
  authDomain: "tallycally-a6711.firebaseapp.com",
  projectId: "tallycally-a6711",
  storageBucket: "tallycally-a6711.appspot.com",
  messagingSenderId: "437622327943",
  appId: "1:437622327943:web:ea083e056cdc2ccd00c622",
  measurementId: "G-FC40TDNK4V"
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

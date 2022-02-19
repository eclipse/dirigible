import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-app.js'

// import { analytics } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-analytics.js'
// import { auth } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-auth.js'
import { getFirestore, collection, getDocs  } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-firestore.js'

const firebaseConfig = {

};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function getCities(db) {
  const citiesCol = collection(db, 'users');
  const citySnapshot = await getDocs(citiesCol);
  const cityList = citySnapshot.docs.map(doc => doc.data());
  return cityList;
}

await getCities(db)
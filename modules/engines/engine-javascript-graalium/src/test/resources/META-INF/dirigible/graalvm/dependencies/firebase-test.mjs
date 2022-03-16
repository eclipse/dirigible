import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-app.js'

// import { getFirestore, collection, getDocs  } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-firestore.js'
import { getFirestore, collection, getDocs } from 'https://www.gstatic.com/firebasejs/9.6.7/firebase-firestore-lite.js';

const firebaseConfig = {
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function getCities(db) {
  const citiesCol = collection(db, 'users');
  const citySnapshot = await getDocs(citiesCol);
  const cityList = citySnapshot.docs.map(doc => doc.data());
  console.log("!!!! VM: data: " + JSON.stringify(cityList, null, 2));
  return cityList;
}

await getCities(db)
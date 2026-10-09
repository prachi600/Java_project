const petsData = [
  {
    id: 1,
    name: "Luna",
    species: "Cat",
    breed: "Persian Cat",
    age: "1 year",
    gender: "Female",
    size: "Small",
    image: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=500&q=80",
    description: "Luna is a calm Persian cat who enjoys sitting on laps and lounging near sunlit windows.",
    vaccinated: "Complete (FVRCP)",
    neutered: "Yes",
    healthNotes: "Long coat requires regular brushing.",
    shelter: "City Animal Rescue",
    temperament: "Calm, Quiet, Affectionate"
  },
  {
    id: 2,
    name: "Coco",
    species: "Rabbit",
    breed: "Holland Lop",
    age: "6 months",
    gender: "Male",
    size: "Small",
    image: "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&w=500&q=80",
    description: "Coco is a floppy-eared rabbit who loves fresh greens and exploring indoor spaces.",
    vaccinated: "Yes (RHDV2)",
    neutered: "No",
    healthNotes: "Dental health verified clear.",
    shelter: "Happy Paws Sanctuary",
    temperament: "Curious, Energetic"
  },
  {
    id: 3,
    name: "Rio",
    species: "Bird",
    breed: "Macaw Parrot",
    age: "4 years",
    gender: "Male",
    size: "Medium",
    image: "https://images.unsplash.com/photo-1552728089-57bdde30beb3?auto=format&fit=crop&w=500&q=80",
    description: "Rio is a colorful, vocal Macaw who can speak a few words and loves perching on shoulders.",
    vaccinated: "Avian Verified",
    neutered: "No",
    healthNotes: "Feather and beak inspection passed.",
    shelter: "Avian Haven Sanctuary",
    temperament: "Playful, Vocal, Intelligent"
  },
  {
    id: 4,
    name: "Peanut",
    species: "Small Pet",
    breed: "Syrian Hamster",
    age: "5 months",
    gender: "Male",
    size: "Small",
    image: "https://images.unsplash.com/photo-1425082661705-1834bfd09dca?auto=format&fit=crop&w=500&q=80",
    description: "Peanut is a cute, active Syrian Hamster who loves running on his wheel and storing seeds.",
    vaccinated: "Health Certified",
    neutered: "No",
    healthNotes: "Active, clear eyes, healthy coat.",
    shelter: "Little Paws Rescue",
    temperament: "Active, Nocturnal, Friendly"
  },
  {
    "id": 5,
    "name": "Muffin",
    "species": "Small Pet",
    "breed": "Netherland Dwarf Rabbit",
    "age": "1 year",
    "gender": "Female",
    "size": "Small",
    "image": "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&w=500&q=80",
    "description": "Muffin is an inquisitive little bunny who loves munching on fresh hay and gentle head scratches.",
    "vaccinated": "Up to Date",
    "neutered": "Yes",
    "healthNotes": "Very healthy, regular checkups completed.",
    "shelter": "Little Paws Rescue",
    "temperament": "Curious, Gentle, Shy"
  }
];

let favorites = [];
let selectedPet = null;

document.addEventListener("DOMContentLoaded", () => {
  renderPets(petsData);

  const searchInput = document.getElementById('search-input');
  const speciesFilter = document.getElementById('species-filter');
  const sizeFilter = document.getElementById('size-filter');

  if (searchInput) searchInput.addEventListener('keyup', filterPets);
  if (speciesFilter) speciesFilter.addEventListener('change', filterPets);
  if (sizeFilter) sizeFilter.addEventListener('change', filterPets);

  document.querySelectorAll('.role-btn').forEach(button => {
    button.addEventListener('click', (e) => {
      switchRole(e.target.dataset.role, e.target);
    });
  });

  const quizBtn = document.getElementById('quiz-btn');
  if (quizBtn) quizBtn.addEventListener('click', runQuiz);

  const closeModalBtn = document.getElementById('close-modal-btn');
  const applyModalBtn = document.getElementById('apply-modal-btn');
  if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
  if (applyModalBtn) applyModalBtn.addEventListener('click', applyFromModal);
});

function renderPets(pets) {
  const container = document.getElementById('pet-container');
  if (!container) return;
  
  container.innerHTML = '';

  if (!pets || pets.length === 0) {
    container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #64748b; padding: 2rem;">No pets found matching your criteria.</p>';
    return;
  }

  pets.forEach(pet => {
    const isFav = favorites.includes(pet.id);
    const card = document.createElement('div');
    card.className = 'pet-card';
    card.innerHTML = `
      <button class="fav-btn ${isFav ? 'active' : ''}" onclick="toggleFav(${pet.id})">♥</button>
      <img src="${pet.image}" alt="${pet.name}" onerror="this.src='https://images.dog.ceo/breeds/beagle/n02088364_11336.jpg'">
      <div class="pet-info">
        <span class="pet-name-title">🐾 ${pet.name}</span>
        <p class="pet-sub">${pet.breed} · ${pet.age}</p>
        <div class="pet-tags">
          <span class="tag">${pet.species}</span>
          <span class="tag">${pet.gender}</span>
          <span class="tag">${pet.size}</span>
          <span class="tag tag-health">Verified</span>
        </div>
        <div class="card-buttons">
          <button class="btn btn-outline" onclick="openModal(${pet.id})">Details</button>
          <button class="btn btn-secondary" onclick="submitApp('${pet.name}')">Apply</button>
        </div>
      </div>
    `;
    container.appendChild(card);
  });
}

function toggleFav(id) {
  if (favorites.includes(id)) {
    favorites = favorites.filter(fId => fId !== id);
  } else {
    favorites.push(id);
  }
  filterPets();
}

function filterPets() {
  const searchEl = document.getElementById('search-input');
  const speciesEl = document.getElementById('species-filter');
  const sizeEl = document.getElementById('size-filter');

  const search = searchEl ? searchEl.value.toLowerCase() : '';
  const species = speciesEl ? speciesEl.value : 'all';
  const size = sizeEl ? sizeEl.value : 'all';

  const filtered = petsData.filter(pet => {
    const matchesSearch = pet.name.toLowerCase().includes(search) || pet.breed.toLowerCase().includes(search);
    const matchesSpecies = species === 'all' || pet.species === species;
    const matchesSize = size === 'all' || pet.size === size;
    return matchesSearch && matchesSpecies && matchesSize;
  });

  renderPets(filtered);
}

function runQuiz() {
  const choice = prompt("Smart Match Quiz:\nSelect preferred category:\n1. Dog\n2. Cat\n3. Rabbit\n4. Reptile\n5. Bird\n6. Small Pet\n\nEnter number (1-6):");
  const speciesMap = { '1': 'Dog', '2': 'Cat', '3': 'Rabbit', '4': 'Reptile', '5': 'Bird', '6': 'Small Pet' };
  
  if (speciesMap[choice]) {
    const speciesFilter = document.getElementById('species-filter');
    if (speciesFilter) {
      speciesFilter.value = speciesMap[choice];
      filterPets();
    }
  }
}

function openModal(id) {
  selectedPet = petsData.find(p => p.id === id);
  if (!selectedPet) return;

  document.getElementById('modal-img').src = selectedPet.image;
  document.getElementById('modal-name').innerText = selectedPet.name;
  document.getElementById('modal-subtext').innerText = `${selectedPet.breed} • ${selectedPet.age} • ${selectedPet.gender} • ${selectedPet.size}`;
  document.getElementById('modal-description').innerText = selectedPet.description;
  document.getElementById('modal-vaccinated').innerText = selectedPet.vaccinated;
  document.getElementById('modal-neutered').innerText = selectedPet.neutered;
  document.getElementById('modal-health-notes').innerText = selectedPet.healthNotes;
  document.getElementById('modal-shelter').innerText = selectedPet.shelter;
  document.getElementById('modal-temperament').innerText = selectedPet.temperament;

  document.getElementById('pet-modal').classList.add('active');
}

function closeModal() {
  document.getElementById('pet-modal').classList.remove('active');
}

function applyFromModal() {
  if (selectedPet) {
    submitApp(selectedPet.name);
    closeModal();
  }
}

function submitApp(petName) {
  const statusText = document.getElementById('app-status-text');
  const trackerBar = document.getElementById('tracker-bar');
  
  if (statusText) {
    statusText.innerHTML = `Application submitted for <strong>${petName}</strong> (Status: Under Review).`;
  }
  if (trackerBar) {
    trackerBar.style.display = 'flex';
  }
  alert(`Your application for ${petName} was submitted successfully!`);
}

function switchRole(role, targetButton) {
  document.querySelectorAll('.dashboard-view').forEach(view => view.classList.remove('active'));
  document.querySelectorAll('.role-switcher button').forEach(btn => btn.classList.remove('active'));
  
  const targetView = document.getElementById(`${role}-dashboard`);
  if (targetView) targetView.classList.add('active');
  if (targetButton) targetButton.classList.add('active');
}
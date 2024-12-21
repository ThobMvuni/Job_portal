// Modal functionality
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const loginModal = document.getElementById('loginModal');
const registerModal = document.getElementById('registerModal');
const closeBtns = document.querySelectorAll('.close');

// Modal event listeners
loginBtn.addEventListener('click', () => {
    loginModal.style.display = 'block';
});

registerBtn.addEventListener('click', () => {
    registerModal.style.display = 'block';
});

closeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        loginModal.style.display = 'none';
        registerModal.style.display = 'none';
    });
});

window.addEventListener('click', (e) => {
    if (e.target === loginModal || e.target === registerModal) {
        loginModal.style.display = 'none';
        registerModal.style.display = 'none';
    }
});

// API endpoints
const API_BASE_URL = 'http://localhost:8080/api';

// Form submission handlers
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.querySelector('input[type="email"]').value;
    const password = e.target.querySelector('input[type="password"]').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            loginModal.style.display = 'none';
            location.reload();
        } else {
            alert(data.message);
        }
    } catch (error) {
        console.error('Login error:', error);
    }
});

document.getElementById('registerForm').addEventListener('submit', (e) => {
    e.preventDefault();
    // Add registration logic here
    alert('Registration functionality will be implemented with backend integration');
});

// Job listing functionality
async function fetchJobs(filters = {}) {
    try {
        const queryParams = new URLSearchParams({
            search: filters.searchTerm || '',
            location: filters.location || '',
            industry: filters.industry || '',
            experience: filters.experience || ''
        });

        const response = await fetch(`${API_BASE_URL}/jobs?${queryParams}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const data = await response.json();

        if (response.ok) {
            return data.content; // Spring Boot returns paginated content
        } else {
            console.error('Error fetching jobs:', data.message);
            return [];
        }
    } catch (error) {
        console.error('Error fetching jobs:', error);
        return [];
    }
}

function createJobCard(job) {
    return `
        <div class="job-card">
            <h3>${job.title}</h3>
            <p><strong>Company:</strong> ${job.company_name}</p>
            <p><strong>Location:</strong> ${job.location}</p>
            <p><strong>Salary:</strong> ${job.salary_range}</p>
            <p>${job.description}</p>
            <button onclick="applyForJob(${job.job_id})" class="apply-btn">Apply Now</button>
        </div>
    `;
}

async function displayJobs(filters = {}) {
    const jobsContainer = document.getElementById('jobsContainer');
    jobsContainer.innerHTML = '<p>Loading jobs...</p>';
    
    const jobs = await fetchJobs(filters);
    if (jobs.length === 0) {
        jobsContainer.innerHTML = '<p>No jobs found matching your criteria.</p>';
        return;
    }
    
    jobsContainer.innerHTML = jobs.map(job => createJobCard(job)).join('');
}

// Search and filter functionality
async function filterJobs() {
    const filters = {
        searchTerm: document.getElementById('jobSearch').value,
        location: document.getElementById('locationSearch').value,
        industry: document.getElementById('industryFilter').value,
        experience: document.getElementById('experienceFilter').value
    };

    await displayJobs(filters);
}

// Event listeners for search and filters
document.getElementById('searchBtn').addEventListener('click', filterJobs);
document.getElementById('industryFilter').addEventListener('change', filterJobs);
document.getElementById('experienceFilter').addEventListener('change', filterJobs);

// Apply for job functionality
async function applyForJob(jobId) {
    const token = localStorage.getItem('token');
    if (!token) {
        loginModal.style.display = 'block';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/jobs/${jobId}/apply`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert('Application submitted successfully!');
        } else {
            const data = await response.json();
            alert('Error submitting application: ' + data.message);
        }
    } catch (error) {
        console.error('Error applying for job:', error);
        alert('Error submitting application. Please try again later.');
    }
}

// Initial job display
displayJobs();

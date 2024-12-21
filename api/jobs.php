<?php
header('Content-Type: application/json');
require_once '../config/db_connect.php';

// Handle CORS
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

$method = $_SERVER['REQUEST_METHOD'];

switch($method) {
    case 'GET':
        getJobs();
        break;
    case 'POST':
        createJob();
        break;
    case 'PUT':
        updateJob();
        break;
    case 'DELETE':
        deleteJob();
        break;
    default:
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
        break;
}

function getJobs() {
    global $pdo;
    
    try {
        // Get filter parameters
        $industry = $_GET['industry'] ?? '';
        $location = $_GET['location'] ?? '';
        $experience = $_GET['experience'] ?? '';
        $search = $_GET['search'] ?? '';
        
        // Build the query
        $query = "SELECT j.*, c.company_name 
                 FROM jobs j 
                 LEFT JOIN companies c ON j.company_id = c.company_id 
                 WHERE j.status = 'active'";
        $params = [];
        
        if ($industry) {
            $query .= " AND j.industry = ?";
            $params[] = $industry;
        }
        if ($location) {
            $query .= " AND j.location LIKE ?";
            $params[] = "%$location%";
        }
        if ($experience) {
            $query .= " AND j.experience_level = ?";
            $params[] = $experience;
        }
        if ($search) {
            $query .= " AND (j.title LIKE ? OR j.description LIKE ?)";
            $params[] = "%$search%";
            $params[] = "%$search%";
        }
        
        $query .= " ORDER BY j.posted_date DESC";
        
        $stmt = $pdo->prepare($query);
        $stmt->execute($params);
        $jobs = $stmt->fetchAll();
        
        echo json_encode(['success' => true, 'data' => $jobs]);
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}

function createJob() {
    global $pdo;
    
    try {
        $data = json_decode(file_get_contents('php://input'), true);
        
        // Validate required fields
        $required = ['company_id', 'title', 'description', 'job_type', 'experience_level'];
        foreach ($required as $field) {
            if (!isset($data[$field])) {
                throw new Exception("Missing required field: $field");
            }
        }
        
        $stmt = $pdo->prepare("INSERT INTO jobs (company_id, title, description, requirements, 
                              salary_range, location, job_type, experience_level, industry, deadline_date) 
                              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        $stmt->execute([
            $data['company_id'],
            $data['title'],
            $data['description'],
            $data['requirements'] ?? null,
            $data['salary_range'] ?? null,
            $data['location'] ?? null,
            $data['job_type'],
            $data['experience_level'],
            $data['industry'] ?? null,
            $data['deadline_date'] ?? null
        ]);
        
        $jobId = $pdo->lastInsertId();
        
        echo json_encode(['success' => true, 'job_id' => $jobId]);
    } catch(Exception $e) {
        http_response_code(400);
        echo json_encode(['error' => $e->getMessage()]);
    }
}

function updateJob() {
    global $pdo;
    
    try {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['job_id'])) {
            throw new Exception("Job ID is required");
        }
        
        $updateFields = [];
        $params = [];
        
        $allowedFields = ['title', 'description', 'requirements', 'salary_range', 
                         'location', 'job_type', 'experience_level', 'industry', 
                         'deadline_date', 'status'];
        
        foreach ($allowedFields as $field) {
            if (isset($data[$field])) {
                $updateFields[] = "$field = ?";
                $params[] = $data[$field];
            }
        }
        
        if (empty($updateFields)) {
            throw new Exception("No fields to update");
        }
        
        $params[] = $data['job_id'];
        
        $stmt = $pdo->prepare("UPDATE jobs SET " . implode(', ', $updateFields) . 
                             " WHERE job_id = ?");
        $stmt->execute($params);
        
        echo json_encode(['success' => true]);
    } catch(Exception $e) {
        http_response_code(400);
        echo json_encode(['error' => $e->getMessage()]);
    }
}

function deleteJob() {
    global $pdo;
    
    try {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['job_id'])) {
            throw new Exception("Job ID is required");
        }
        
        // Soft delete by updating status to 'closed'
        $stmt = $pdo->prepare("UPDATE jobs SET status = 'closed' WHERE job_id = ?");
        $stmt->execute([$data['job_id']]);
        
        echo json_encode(['success' => true]);
    } catch(Exception $e) {
        http_response_code(400);
        echo json_encode(['error' => $e->getMessage()]);
    }
}
?>

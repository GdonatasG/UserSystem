<?php  
$servername = "localhost";  
$username = "db username";  
$password = "db password";  
$database = "db table";  
$conn = new mysqli($servername, $username, $password, $database);  
if ($conn->connect_error) {  
    die("Connection failed: " . $conn->connect_error);  
}  
?> 

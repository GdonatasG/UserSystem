<?php 
 
 require_once 'connection.php';
 
 $response = array();
 
 if(isset($_GET['apicall'])){
 
 switch($_GET['apicall']){
 
 case 'signup':
 if(isTheseParametersAvailable(array('username','email','password'))){
 $username = $_POST['username']; 
 $email = $_POST['email']; 
 $password = md5($_POST['password']); 
 
 $stmt = $conn->prepare("SELECT id FROM user WHERE username = ? OR email = ?");
 $stmt->bind_param("ss", $username, $email);
 $stmt->execute();
 $stmt->store_result();
 
 if($stmt->num_rows > 0){
 $response['error'] = true;
 $response['message'] = 'Toks vartotojas jau egzistuoja!';
 $stmt->close();
 }else{
 $stmt = $conn->prepare("INSERT INTO user (username, email, password) VALUES (?, ?, ?)");
 $stmt->bind_param("sss", $username, $email, $password);
 
 if($stmt->execute()){
 $stmt = $conn->prepare("SELECT id, id, username, email FROM user WHERE username = ?"); 
 $stmt->bind_param("s",$username);
 $stmt->execute();
 $stmt->bind_result($userid, $id, $username, $email);
 $stmt->fetch();
 
 $user = array(
 'id'=>$id, 
 'username'=>$username, 
 'email'=>$email
 );
 
 $stmt->close();
 
 $response['error'] = false; 
 $response['message'] = 'Registracija sėkminga!'; 
 $response['user'] = $user; 
 }
 }
 
 }else{
 $response['error'] = true; 
 $response['message'] = 'required parameters are not available'; 
 }
 
 break; 
 
 case 'login':
 
 if(isTheseParametersAvailable(array('username','password'))){
 
 $username = $_POST['username'];
 $password = md5($_POST['password']);
 
 $stmt = $conn->prepare("SELECT id, username, email FROM user WHERE username = ? AND password = ?");
 $stmt->bind_param("ss",$username,$password);
 
 $stmt->execute();
 
 $stmt->store_result();
 
 if($stmt->num_rows > 0){
 
 $stmt->bind_result($id, $username, $email);
 $stmt->fetch();
 
 $user = array(
 'id'=>$id, 
 'username'=>$username, 
 'email'=>$email
 );
 
 $response['error'] = false; 
 $response['message'] = 'Prisijungėte!'; 
 $response['user'] = $user; 
 }else{
 $response['error'] = false; 
 $response['message'] = 'Netinkamas vardas arba slaptažodis';
 }
 }
 break; 
 
 case 'changename':
 
 if(isTheseParametersAvailable(array('id','newusername'))){
 
 $id = $_POST['id'];
 $newusername = $_POST['newusername'];
 
 $stmt = $conn->prepare("SELECT id FROM user WHERE username = ?");
 $stmt->bind_param("s",$newusername);
 
 $stmt->execute();
 
 $stmt->store_result();
 
 if($stmt->num_rows > 0){
     
    $response['error'] = false; 
    $response['message'] = 'Vartotojas su tokiu vardu jau egzistuoja!';
 }else{
 $stmt = $conn->prepare("UPDATE user SET username = ? WHERE id = ?");
 $stmt->bind_param("ss",$newusername,$id);
 
 if($stmt->execute()){
     $stmt = $conn->prepare("SELECT id, username, email FROM user WHERE id = ?");
 $stmt->bind_param("s",$id);
 $stmt->bind_result($id, $newusername, $email);
 $stmt->fetch();
 
 $user = array(
 'id'=>$id, 
 'username'=>$newusername,
 'email'=>$email
 );
 
 $stmt->close();
 
 $response['error'] = false; 
 $response['message'] = 'Vardas sėkmingai pakeistas!'; 
 $response['user'] = $user; 
 }
 
 }
 }
 break; 
 
 default: 
 $response['error'] = true; 
 $response['message'] = 'Invalid Operation Called';
 }
 
 }else{
 $response['error'] = true; 
 $response['message'] = 'Invalid API Call';
 }
 
 echo json_encode($response);
 
 function isTheseParametersAvailable($params){
 
 foreach($params as $param){
 if(!isset($_POST[$param])){
 return false; 
 }
 }
 return true; 
 }
 ?>

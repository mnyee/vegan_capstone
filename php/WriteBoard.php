<?php
 
 $con = mysqli_connect("localhost","chad76","asdf070612","chad76");
 
 
 
 $bulletinTitle = $_POST["bulletinTitle"];
 
 $bulletinSubject = $_POST["bulletinSubject"];
 
 $bulletinGrade = $_POST["bulletinGrade"];
 
 $bulletinContent = $_POST["bulletinContent"];
 
 
 
 $statemente = mysqli_prepare($con, "INSERT INTO BOARD VALUES(?, ?, ?, ?)");
 
 mysqli_stmt_bind_param($statemente, "ssss", $bulletinTitle, $bulletinSubject, $bulletinGrade, $bulletinContent);
 
 mysqli_stmt_execute($statemente);
 
 
 
 $response = array();
 
 $response["success"] = true;
 
 
 
 echo json_encode($response);
 
?>

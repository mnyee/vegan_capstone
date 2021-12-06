<?php
    $db_host="localhost";
    $db_user="root";
    $db_password="";
    $db_name="vege";

    $con=mysqli_connect($db_host, $db_user, $db_password, $db_name);
    if (mysqli_connect_error()) {
        $response["success"] = false;
        $response["msg"] = "DB 접속 실패.";
        $response["errcode"] = 2020;
        echo json_encode($response);
        exit();
    }
    
?>
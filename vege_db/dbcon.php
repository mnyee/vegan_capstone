<?php
    $db_host="localhost";
    $db_user="root";
    $db_password="1234";
    $db_name="vege";

    $con=mysqli_connect($db_host, $db_user, $db_password, $db_name);
    if (mysqli_connect_error()) {
        $response["success"] = false;
        $response["msg"] = "DB 접속 실패.";
        $response["errcode"] = 2023;
        echo json_encode($response);
        exit();
    }
    //echo "MySQL 접속 성공";
    //mysqli_close($con);
    
?>
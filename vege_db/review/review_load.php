<?php
// 리뷰 조회
    //header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php"; // db 연결

    // 서버로부터 좌표값과 함께 응답 요청
    $user_lng = $_POST["user_lng"]; // 경도
    $user_lat = $_POST["user_lat"]; // 위도

    // restaurant num 구하기
    $sql1 = "SELECT num FROM restaurant WHERE `lng` = $user_lng AND `lat` = $user_lat;"; 
    $result = mysqli_query($con, $sql1);
    $data = mysqli_fetch_row($result);

    $resNum=$data[0];
    //$resNum=1;
    $sql2 = "SELECT title, body, img WHERE `restaurant` = $resNum;";
    $result = mysqli_query($con, $sql2);    
    $row = mysqli_fetch_assoc($result);
    $response["data"]=$row;
    echo json_encode($response);
    mysqli_close($con);
?>

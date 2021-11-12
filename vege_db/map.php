<?php
// 서버에 식당 정보 응답

    include "dbcon.php"; //db 연결
    
    // 서버로부터 좌표값과 함께 응답 요청
    $user_long = $_POST["user_long"]; // 경도
    $user_lat = $_POST["user_lat"]; // 위도

    $sql = "SELECT name, type, tel, address, menu 
            FROM restaurant 
            WHERE long = $user_long AND lat = $user_lat";

    $ret = mysqli_query($con, $sql);
    if($ret){
        $count = mysqli_num_rows($ret);
    }
    else{
        echo "조회 결과 없습니다."."<br>";
        echo "오류 원인:".mysqli_error($con);
        exit();
    }

    while($row = mysqli_fetch_array($ret)) {
        echo json_encode($row);
    }
    header("Content-Type: application/json");
    $json=json_encode($ret);
    if($json === false){
        $json = json_encode(array("jsonError", json_last_error_msg()));
        if($json === false) {
            $json = '{"jsonError": "unknown"}';            
        }
        http_response_code(500);     
    }
    echo $json;
       
?>
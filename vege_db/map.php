<?php
    
// 서버에 식당 정보 응답
    header("Content-Type: application/json; charset=utf-8");
    include "dbcon.php"; //db 연결
    
    // 서버로부터 좌표값과 함께 응답 요청
    $user_long = $_POST["user_long"]; // 경도
    $user_lat = $_POST["user_lat"]; // 위도

    //위,경도 데이터 검증
    if (!is_numeric($user_long)){
        $response["success"] = false;
        $response["msg"] = "경도가 잘못됨";
        $response["errcode"] = 2031;
        echo json_encode($response);
        exit();

    }

    if (!is_numeric($user_lat)){
        $response["success"] = false;
        $response["msg"] = "위도가 잘못됨";
        $response["errcode"] = 2032;
        echo json_encode($response);
        exit();

    }

    $sql = "SELECT name, type, tel, address, menu 
            FROM restaurant 
            WHERE `long` = $user_long AND `lat` = $user_lat";
    //echo $sql;

    $ret = mysqli_query($con, $sql);
    if($ret){
        $count = mysqli_num_rows($ret);
    }
    else{
        $response["success"] = false;
        $response["msg"] = "조회결과 없음.";
        $response["errcode"] = 2033;
        echo json_encode($response);
        exit();
    }

    
    // $row를 $data에 2차원 배열로. 
    $data = array();
    while($row = mysqli_fetch_assoc($ret)) {
        array_push($data, $row);        
    }

    $response["success"] = true;
    $response["data"] = $data;
    echo json_encode($response);

   
    // $json=json_encode($ret);
    // if($json === false){
    //     $json = json_encode(array("jsonError", json_last_error_msg()));
    //     if($json === false) {
    //         $json = '{"jsonError": "unknown"}';            
    //     }
    //     http_response_code(500);     
    // }
    // echo $json;
       
?>
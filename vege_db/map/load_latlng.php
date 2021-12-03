<?php
// 위도 경도(마커 찍기)
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php";

    //$now_lat = $_POST["lat"];
    //$now_lng = $_POST["lng"];

    $sql = "SELECT lng, lat FROM restaurant WHERE `district` = '영등포구' or `district` = '관악구' or `district` = '서초구'";
    //echo $sql;

    $result = mysqli_query($con, $sql);
    if(!$result){
        $response["success"] = false;
        $response["msg"] = "조회결과 없음.";
        $response["errcode"] = 2034;
        echo json_encode($response);
        exit();
    }

    
    // $row를 $data에 2차원 배열로. 
    $data = array();
    while($row = mysqli_fetch_assoc($result)) {
        array_push($data, $row);        
    }

    $response["success"] = true;
    $response["data"] = $data;
    echo json_encode($response);
?>
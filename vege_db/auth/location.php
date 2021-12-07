<?php
// 위치 인증: 클라이언트로부터 POST 메소드로 district가 넘어와서 sql로 같은 distric에 있는 식당 정보(json)를 넘겨줌.
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php"; // db 연결

    $district = $_POST["district"];


    // 데이터 검증
    if(strlen($district)==0){
        $response["success"] = false;
        $response["msg"] = "데이터가 올바르지 않습니다.";
        $response["errcode"] = 2060;
        echo json_encode($response);
        exit();
    }
    $sql = "SELECT * FROM restaurant WHERE `district` = '$district';";
    
    $result = mysqli_query($con, $sql);    

    if(!$result){
        $response["success"] = false;
        $response["msg"] = "district sql 실행 실패.";
        $response["errcode"] = 2061;
        echo json_encode($response);
        exit();
    }

    $data = array();
    while($row = mysqli_fetch_assoc($result)) {
        array_push($data, $row);        
    }

    $response["success"] = true;
    $response["data"] = $data;
    echo json_encode($response);
    mysqli_close($con);
?>



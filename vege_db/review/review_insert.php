<?php
// 리뷰 등록
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php";
    
    $title = $_POST["title"]; // 제목
    $body = $_POST["body"]; // 본문
    

    // 제목, 본문 문자열 길이 제한
    if(strlen($title) >11){// or strlen($title)==0){
        $response["success"] = false;
        $response["msg"] = "제목을 10자 이내로 작성해주세요.";
        $response["errcode"] = 2040;
        echo json_encode($response);
        exit();
    }

    if(strlen($title)==0){
        $response["success"] = false;
        $response["msg"] = "제목을 작성해주세요.";
        $response["errcode"] = 2041;
        echo json_encode($response);
        exit();
    }

    if(strlen($body) >101){
        $response["success"] = false;
        $response["msg"] = "리뷰를 100자 이내로 작성해주세요.";
        $response["errcode"] = 2042;
        echo json_encode($response);
        exit();
    }
    
    $sql1 = "INSERT INTO review (`title`, `body`) VALUES ('$title', '$body')";
    $result = mysqli_query($con, $sql1);    

    if(!$result){
        $response["success"] = false;
        $response["msg"] = "리뷰 등록 실패.";
        $response["errcode"] = 2043;
        echo json_encode($response);
        exit();
    }

    // num 구하기
    $sql2 = "SELECT num FROM review ORDER BY `num` DESC LIMIT 1";
    $result = mysqli_query($con, $sql2);
    // $data = array();
    // while($row = mysqli_fetch_assoc($result)) {
    //     array_push($data, $row);        
    // }

    $data = mysqli_fetch_row($result);
    $num=$data[0];
    
   
    // 리뷰 등록
    $sql3 = "SELECT title, body FROM review WHERE `num` = $num";
    $result = mysqli_query($con, $sql3);

    $data = array();
    while($row = mysqli_fetch_assoc($result)) {
        array_push($data, $row);        
    }

    $response["success"] = true;
    $response["data"] = $data;
    echo json_encode($response);
    mysqli_close($con);

    
?>
<?php
// 리뷰 수정
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php";

    $num = $_POST["num"]; // 리뷰 고유 번호
    $title = $_POST["title"]; // 제목
    $body = $_POST["body"]; // 본문

    // 제목, 본문 문자열 길이 제한
    if(strlen($title) >11 or strlen($title)==0){
        $response["success"] = false;
        $response["msg"] = "제목을 10자 이내로 작성해주세요.";
        $response["errcode"] = 2040;
        echo json_encode($response);
        exit();
    }

    if(strlen($body) >101){
        $response["success"] = false;
        $response["msg"] = "리뷰를 100자 이내로 작성해주세요.";
        $response["errcode"] = 2041;
        echo json_encode($response);
        exit();
    }

    $sql1 = "UPDATE review SET `title`='$title', `body`='$body' WHERE (`num`='$num')";
    $result = mysqli_query($con, $sql1);    

    if(!$result){
        $response["success"] = false;
        $response["msg"] = "리뷰 수정 실패.";
        $response["errcode"] = 2043;
        echo json_encode($response);
        exit();
    }

    $sql2 = "SELECT title, body FROM review WHERE `num` = $num";
    $result = mysqli_query($con, $sql2);

    $data = mysqli_fetch_row($result);

    $response["success"] = true;
    $response["data"] = $data;
    echo json_encode($response);
    mysqli_close($con);


?>
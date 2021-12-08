<?php
// 리뷰 삭제
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php";

    $num = $_POST["num"]; // 리뷰 고유 번호
    $title = $_POST["title"]; // 제목
    $body = $_POST["body"]; // 본문
    $phone = $_POST["phone"]; // 유저 핸드폰 번호

    /*
    $num = $_GET["num"]; // 리뷰 고유 번호
    $title = $_GET["title"]; // 제목
    $body = $_GET["body"]; // 본문
    $phone = $_GET["phone"]; // 유저 핸드폰 번호  
    */

    $sql = "DELETE FROM review WHERE `num`='$num' and `phone` = '$phone'";
    $result = mysqli_query($con, $sql);

    if(!$result){
        $response["success"] = false;
        $response["msg"] = "리뷰 삭제 실패.";
        $response["errcode"] = 2044;
        echo json_encode($response);
        exit();
    }
    else{
        $response["success"] = true;
        //$response["data"] = $data;
        echo json_encode($response);
    
    }
    mysqli_close($con);

    

?>
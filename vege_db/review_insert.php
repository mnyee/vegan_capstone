<?php
// 리뷰 저장
    include "dbcon.php";
    
    $review = $_POST["review"];

    $sql = "INSERT INTO person VALUES ('$review')";
    $ret = mysqli_query($con, $sql);
    
    if($ret){
        echo "리뷰 등록을 실패했습니다."."<br>";
        echo "오류 원인:".mysqli_error($con);
        exit();
    }
    mysqli_close($con);
?>
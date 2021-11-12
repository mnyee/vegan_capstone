<?php
    $con = mysqli_connect("?", "?", "?", "?");//"서버 ip", "데이터베이스 아이디", "비번", "데이터베이스"
    mysqli_query($con,'SET NAMES utf8');

    $num = $_POST["num"];

  	$startdate = [];
	$query = "SELECT startdate FROM  `Diary$num`";
	$result = mysqli_query($con, $query);

	$response["success"] = false;

	while ($row = mysqli_fetch_array($result)) {
	$response["success"] = true;
    $response[] = $row["startdate"];
	}

echo json_encode($response);

?>

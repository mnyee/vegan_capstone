<?php
// 리뷰 등록
    header("Content-Type: application/json; charset=utf-8");
    include "../dbcon.php"; // db 연결
    //include "image_upload.php"; // 이미지 업로드
    
    $title = $_POST["title"]; // 제목
    $body = $_POST["body"]; // 본문    
    
    $tempFile = $_FILES['imgFile']['tmp_name']; // $tempFile="D:\xampp\tmp\phpA3BD.tmp" 
    $fileTypeExt = explode("/", $_FILES['imgFile']['type']); //$fileTypeExt=[image, jpeg]
    $fileType = $fileTypeExt[0]; //$fileType = image
    $fileExt = $fileTypeExt[1]; //fileExt = jpeg
    $extStatus = false;

    // image 데이터 검증(크기, 확장자, 이미지파일, 개수(3개))
    switch($fileExt){
        case 'jpeg':
        case 'jpg':
        case 'gif':
        case 'bmp':
        case 'png':
            $extStatus = true;
            break;
        
        default:
            echo "이미지 전용 확장자(jpg, bmp, gif, png)외에는 사용이 불가합니다."; 
            exit;
            break;
    }

    // 이미지 파일이 맞는지 검사. 
    if($fileType == 'image'){
        // 허용할 확장자를 jpg, bmp, gif, png로 정함, 그 외에는 업로드 불가
        if($extStatus){
            // 임시 파일을 옮길 디렉토리 및 파일명
            $resFile = "../../img/{$_FILES['imgFile']['name']}";
            $resFileName = $_FILES['imgFile']['name'];

            // 임시 저장된 파일을 우리가 저장할 디렉토리 및 파일명으로 옮김
            $imageUpload = move_uploaded_file($tempFile, $resFile);
            
            // 업로드 성공 여부 확인(json 오류 코드)
            if($imageUpload == false){
                $response["success"] = false;
                $response["msg"] = "파일 업로드에 실패하였습니다.";
                $response["errcode"] = 2050;
                echo json_encode($response);
                exit();
            }
        }	
        else {
            $response["success"] = false;
            $response["msg"] = "파일 확장자는 jpg, bmp, gif, png 이어야 합니다.";
            $response["errcode"] = 2051;
            echo json_encode($response);
            exit();
        }	
    }	
    else {
        $response["success"] = false;
        $response["msg"] = "이미지 파일이 아닙니다.";
        $response["errcode"] = 2052;
        echo json_encode($response);
        exit();
    }
    

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
    
    $sql1 = "INSERT INTO review (`title`, `body`, `image`) VALUES ('$title', '$body', '$resFileName');";
    echo "$sql1";
    $result = mysqli_query($con, $sql1);    

    if(!$result){
        $response["success"] = false;
        $response["msg"] = "리뷰 등록 실패.";
        $response["errcode"] = 2043;
        echo json_encode($response);
        exit();
    }

    // num 구하기
    $sql2 = "SELECT num FROM review ORDER BY `num` DESC LIMIT 1"; // WHERE $title? 
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
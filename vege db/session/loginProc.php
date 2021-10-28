<?
    include "lib.php"; 

    // login.php에서 uid와 pwd를 받아온다. 
    $uid = $_POST['uid'];
    $pwd = $_POST['pwd'];

    $uid = mysqli_real_escape_string($connect,$uid); 
    $pwd = mysqli_real_escape_string($connect,$pwd); 

    // db에서 로그인 정보 가져오기
    $query = "select * from members where id='$uid' and pwd='$pwd' ";
    // 실제 값 가져오기
    $result = mysqli_query($connect, $query);
    // result를 배열로 변환하기
    $data = mysqli_fetch_array($result);
    

    if($data){ // data에 값이 있다면 
        // 세션에 로그인한 시간을 등록
        $_SESSION['isLogin'] = time(); 
        print_r("성공");
        print_r("$data");

    }else{

        echo "로그인정보가 올바르지 않습니다.";
    }

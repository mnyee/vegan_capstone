<?
    include "lib.php"; // 모든 페이지에서 mysql 연결과 session_start가 동일하게 사용됨.

    $isLogin = $_SESSION['isLogin'];

    if($isLogin){ 
        ?>
        로그인 후 이용해주세요. <br>
        <a href="login.php">로그인</a>


    }
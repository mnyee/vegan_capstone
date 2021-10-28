<?php
    $con=mysqli_connect("localhost","root","") or die("MariaDB 접속 실패");
    phpinfo();
    mysqli_close($con);
?>

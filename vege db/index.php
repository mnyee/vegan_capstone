<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>

<?php
    require_once('db class.php');

    $db = new MysqliDb ('localhost', 'root', 'gpal5092', 'vege');
    $restau = $db->get('restaurant');
    print_r($restau);
?>
    
</body>
</html>



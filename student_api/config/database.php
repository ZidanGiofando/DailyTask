<?php

class Database
{
    private $host = "localhost";
    private $username = "root";
    private $password = "";
    private $database = "campus_db";

    public function connect()
    {
        $connection = new mysqli(
            $this->host,
            $this->username,
            $this->password,
            $this->database
        );

        if ($connection->connect_error) {
            die("Database gagal terkoneksi");
        }

        return $connection;
    }
}
<?php

class Student
{
    private $connection;
    private $table = "students";

    public function __construct($connection)
    {
        $this->connection = $connection;
    }

    public function create($name, $email)
    {
        $sql = "INSERT INTO {$this->table} 
                (student_name, student_email) 
                VALUES (?, ?)";

        $stmt = $this->connection->prepare($sql);

        if (!$stmt) {
            return false;
        }

        $stmt->bind_param("ss", $name, $email);

        $result = $stmt->execute();

        $stmt->close();

        return $result;
    }

    public function getAll()
    {
        $sql = "SELECT 
                    id,
                    student_name,
                    student_email
                FROM {$this->table}
                ORDER BY id DESC";

        $stmt = $this->connection->prepare($sql);

        if (!$stmt) {
            return [];
        }

        $stmt->execute();

        $result = $stmt->get_result();

        $students = [];

        while ($row = $result->fetch_assoc()) {
            $row['id'] = (int) $row['id'];
            $students[] = $row;
        }

        $stmt->close();

        return $students;
    }
}
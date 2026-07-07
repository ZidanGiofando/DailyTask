-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 04, 2026 at 10:27 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dailytask_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `tasks`
--

CREATE TABLE `tasks` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text NOT NULL,
  `deadline` date NOT NULL,
  `time` time NOT NULL,
  `priority` enum('High','Medium','Low') NOT NULL DEFAULT 'Medium',
  `status` enum('pending','done') NOT NULL DEFAULT 'pending',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tasks`
--

INSERT INTO `tasks` (`id`, `user_id`, `title`, `description`, `deadline`, `time`, `priority`, `status`, `created_at`) VALUES
(1, 1, 'moprog uas', 'uas kali ini akan membuat app untuk menyelesaikan masalah sehari hari seperti ini', '2026-07-03', '18:05:00', 'Medium', 'done', '2026-07-03 18:05:47'),
(2, 1, 'dawdwa', 'dawdwa', '2026-07-03', '18:17:00', 'Medium', 'done', '2026-07-03 18:17:31'),
(3, 1, 'ffffff', 'fff', '2026-07-03', '19:02:00', 'Medium', 'done', '2026-07-03 19:02:35'),
(4, 2, 'fsefse', 'fesfes', '2026-07-03', '19:26:00', 'Medium', 'done', '2026-07-03 19:26:34'),
(6, 2, 'dawdwwadd', 'dwadwad', '2026-07-04', '14:38:00', 'Low', 'done', '2026-07-04 14:38:42'),
(7, 2, 'dadwadawd', 'dawdwadwad', '2026-07-04', '14:39:00', 'Medium', 'done', '2026-07-04 14:39:23'),
(8, 2, 'dawdw', 'dawdaw', '2026-07-04', '14:45:00', 'High', 'done', '2026-07-04 14:45:22'),
(9, 3, 'moprog uas', 'uas kali ini akan membuat app untuk menyelesaikan masalah sehari hari seperti ini', '2026-07-10', '00:00:00', 'Low', 'done', '2026-07-04 15:10:47'),
(10, 3, 'bahasa Indonesia', 'halo', '2026-07-04', '15:11:00', 'Low', 'pending', '2026-07-04 15:11:48'),
(11, 3, 'test', 'aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aku AA ariwibowo ! apakah a ada! Aaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa AAMIIN Aaa Aaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaaaaaaaaa aaa', '2026-07-04', '15:25:00', 'High', 'done', '2026-07-04 15:25:50');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `created_at`) VALUES
(1, 'zidan giofando', 'zidan@gmail.com', '$2y$10$HGEFeHfBzl3VXxQYx7IBs.up/QtEsY1vXhnagVESi10/NfD1kRc12', '2026-07-03 18:05:47'),
(2, 'najwa', 'najwa@gmail.com', '$2y$10$Dgmsya8knjXM.41wUVJQgeOx/bWQoji83ZXbPUkBP72/TskPbX6oG', '2026-07-03 19:25:59'),
(3, 'Zidan Bulukumba', 'a@gmail.com', '$2y$10$xwIWHMOLllCmIKh0CFwRY.KCx705NwnG2vx33qZr5.FdUH2NWLyDO', '2026-07-04 15:09:22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_tasks_user` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tasks`
--
ALTER TABLE `tasks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `fk_tasks_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

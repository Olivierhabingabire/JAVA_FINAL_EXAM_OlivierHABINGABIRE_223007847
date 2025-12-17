-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 17, 2025 at 05:43 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sport_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `goals`
--

DROP TABLE IF EXISTS `goals`;
CREATE TABLE IF NOT EXISTS `goals` (
  `goal_id` int NOT NULL AUTO_INCREMENT,
  `match_id` int DEFAULT NULL,
  `player_id` int DEFAULT NULL,
  `team_id` int DEFAULT NULL,
  `minute_scored` int DEFAULT NULL,
  `goal_type` enum('penalty','own goal','normal') DEFAULT 'normal',
  PRIMARY KEY (`goal_id`),
  KEY `match_id` (`match_id`),
  KEY `player_id` (`player_id`),
  KEY `team_id` (`team_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `goals`
--

INSERT INTO `goals` (`goal_id`, `match_id`, `player_id`, `team_id`, `minute_scored`, `goal_type`) VALUES
(1, 1, 1, 1, 12, 'normal'),
(2, 1, 1, 1, 65, 'penalty'),
(3, 1, 5, 4, 72, 'normal'),
(4, 3, 4, 3, 38, 'own goal'),
(5, 4, 5, 4, 22, 'normal');

-- --------------------------------------------------------

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
CREATE TABLE IF NOT EXISTS `matches` (
  `match_id` int NOT NULL AUTO_INCREMENT,
  `sport_id` int DEFAULT NULL,
  `team_home_id` int DEFAULT NULL,
  `team_away_id` int DEFAULT NULL,
  `match_date` timestamp NULL DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `referee_id` int DEFAULT NULL,
  `status` enum('scheduled','ongoing','completed') DEFAULT 'scheduled',
  `home_score` int DEFAULT '0',
  `away_score` int DEFAULT '0',
  PRIMARY KEY (`match_id`),
  KEY `sport_id` (`sport_id`),
  KEY `team_home_id` (`team_home_id`),
  KEY `team_away_id` (`team_away_id`),
  KEY `referee_id` (`referee_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `matches`
--

INSERT INTO `matches` (`match_id`, `sport_id`, `team_home_id`, `team_away_id`, `match_date`, `location`, `referee_id`, `status`, `home_score`, `away_score`) VALUES
(1, 1, 1, 4, '2025-09-25 13:30:00', 'Amahoro Stadium', 1, 'completed', 3, 2),
(2, 2, 2, 3, '2025-09-28 16:00:00', 'Ruhango Arena', 2, 'scheduled', 0, 0),
(3, 3, 3, 1, '2025-10-01 14:00:00', 'Bugesera Court', 3, 'completed', 1, 3),
(4, 1, 4, 1, '2025-10-04 12:30:00', 'Gasabo Stadium', 4, 'ongoing', 1, 1),
(5, 4, 5, 2, '2025-10-07 08:00:00', 'Tennis Center Nyamirambo', 5, 'scheduled', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `match_players`
--

DROP TABLE IF EXISTS `match_players`;
CREATE TABLE IF NOT EXISTS `match_players` (
  `match_player_id` int NOT NULL AUTO_INCREMENT,
  `match_id` int DEFAULT NULL,
  `player_id` int DEFAULT NULL,
  `team_id` int DEFAULT NULL,
  `is_starting` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`match_player_id`),
  KEY `match_id` (`match_id`),
  KEY `player_id` (`player_id`),
  KEY `team_id` (`team_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `match_players`
--

INSERT INTO `match_players` (`match_player_id`, `match_id`, `player_id`, `team_id`, `is_starting`) VALUES
(1, 1, 1, 1, 1),
(2, 1, 2, 1, 1),
(3, 2, 3, 2, 1),
(4, 3, 4, 3, 1),
(5, 4, 5, 4, 1);

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
CREATE TABLE IF NOT EXISTS `players` (
  `player_id` int NOT NULL AUTO_INCREMENT,
  `team_id` int DEFAULT NULL,
  `sport_id` int DEFAULT NULL,
  `player_name` varchar(150) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `nationality` varchar(100) DEFAULT NULL,
  `jersey_number` int DEFAULT NULL,
  PRIMARY KEY (`player_id`),
  KEY `team_id` (`team_id`),
  KEY `sport_id` (`sport_id`)
) ENGINE=MyISAM AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`player_id`, `team_id`, `sport_id`, `player_name`, `position`, `age`, `nationality`, `jersey_number`) VALUES
(1, 1, 1, 'Patrick Mugisha', 'Striker', 25, 'Rwandan', 9),
(2, 1, 1, 'Jean Claude Habimana', 'Goalkeeper', 28, 'Rwandan', 1),
(3, 2, 2, 'Brian Niyitegeka', 'Point Guard', 23, 'Rwandan', 7),
(4, 3, 3, 'Alain Nkurunziza', 'Spiker', 26, 'Rwandan', 12),
(5, 4, 1, 'Eric Rukundo', 'Midfielder', 24, 'Rwandan', 8),
(6, 1, 1, 'Samuel Hakizimana', 'Defender', 27, 'Rwandan', 4),
(7, 1, 1, 'Kevin Nkurikiyinka', 'Midfielder', 24, 'Rwandan', 10),
(8, 1, 1, 'Alex Murenzi', 'Winger', 22, 'Rwandan', 11),
(9, 1, 1, 'Innocent Byiringiro', 'Center Back', 29, 'Rwandan', 5),
(10, 1, 1, 'Claude Ndahimana', 'Defender', 25, 'Rwandan', 2),
(11, 1, 1, 'Eric Mugabo', 'Forward', 23, 'Rwandan', 17),
(13, 1, 1, 'Isaac Ndayisenga', 'Left Back', 25, 'Rwandan', 15),
(14, 4, 1, 'Didier Rukundo', 'Goalkeeper', 29, 'Rwandan', 1),
(15, 4, 1, 'Moses Niyonzima', 'Defender', 24, 'Rwandan', 4),
(16, 4, 1, 'Alex Tuyishime', 'Striker', 22, 'Rwandan', 9),
(17, 4, 1, 'Daniel Uwera', 'Midfielder', 23, 'Rwandan', 8),
(18, 4, 1, 'Patrick Niyomugabo', 'Forward', 26, 'Rwandan', 10),
(19, 4, 1, 'Claude Iradukunda', 'Defender', 27, 'Rwandan', 6),
(20, 4, 1, 'John Mugenzi', 'Winger', 21, 'Rwandan', 11),
(21, 4, 1, 'Elias Nshimiyimana', 'Midfielder', 24, 'Rwandan', 7),
(22, 2, 2, 'Moses Hakizimana', 'Shooting Guard', 25, 'Rwandan', 12),
(23, 2, 2, 'Patrick Mugwaneza', 'Small Forward', 23, 'Rwandan', 8),
(24, 2, 2, 'John Bosco', 'Power Forward', 27, 'Rwandan', 5),
(25, 2, 2, 'Eric Ntaganda', 'Center', 29, 'Rwandan', 13),
(26, 2, 2, 'Kevin Manzi', 'Point Guard', 22, 'Rwandan', 4),
(27, 2, 2, 'Jean Pierre Kamanzi', 'Shooting Guard', 25, 'Rwandan', 9),
(28, 2, 2, 'David Mutabazi', 'Small Forward', 24, 'Rwandan', 7),
(29, 3, 3, 'Jean Claude Niyonsaba', 'Setter', 26, 'Rwandan', 5),
(30, 3, 3, 'Alphonse Tuyisenge', 'Libero', 25, 'Rwandan', 1),
(31, 3, 3, 'Pascal Ndagijimana', 'Outside Hitter', 23, 'Rwandan', 8),
(32, 3, 3, 'Didier Mugisha', 'Middle Blocker', 27, 'Rwandan', 10),
(33, 3, 3, 'Elie Nshimiyimana', 'Opposite Hitter', 24, 'Rwandan', 12),
(34, 3, 3, 'Francis Uwitonze', 'Libero', 28, 'Rwandan', 9),
(35, 5, 4, 'Eden Uwase', 'Singles Player', 21, 'Rwandan', 1),
(36, 5, 4, 'Claudine Umutoni', 'Doubles Player', 23, 'Rwandan', 2),
(37, 5, 4, 'Patrick Niyonzima', 'Singles Player', 26, 'Rwandan', 3),
(38, 5, 4, 'Eric Ndayambaje', 'Doubles Player', 27, 'Rwandan', 4),
(39, 5, 4, 'Linda Uwimana', 'Singles Player', 22, 'Rwandan', 5),
(40, 1, NULL, 'Alex Murenzi', 'Winger', 22, 'Rwandan', 11),
(41, 1, NULL, 'Alex Mizero', 'Winger', 22, 'Rwandan', 11),
(42, 1, NULL, 'Anicet MUHIRE', 'Center back', 28, 'Rwandan', 21),
(43, 1, NULL, 'Alex Mizero', 'Center back', 28, 'Rwandan', 21);

-- --------------------------------------------------------

--
-- Table structure for table `referees`
--

DROP TABLE IF EXISTS `referees`;
CREATE TABLE IF NOT EXISTS `referees` (
  `referee_id` int NOT NULL AUTO_INCREMENT,
  `referee_name` varchar(100) NOT NULL,
  `year_of_experience` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`referee_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `referees`
--

INSERT INTO `referees` (`referee_id`, `referee_name`, `year_of_experience`) VALUES
(1, 'Samuel Uwimana', 5),
(2, 'Jean Marie Nshimiyimana', 3),
(3, 'David Kamanzi', 8),
(4, 'Pauline Ingabire', 2),
(5, 'Richard Byiringiro', 6),
(6, 'Samuel Uwimana', 5),
(7, 'Patience RULISA', 11);

-- --------------------------------------------------------

--
-- Table structure for table `sports`
--

DROP TABLE IF EXISTS `sports`;
CREATE TABLE IF NOT EXISTS `sports` (
  `sport_id` int NOT NULL AUTO_INCREMENT,
  `sport_name` varchar(100) NOT NULL,
  `description` text,
  `rules` text,
  PRIMARY KEY (`sport_id`),
  UNIQUE KEY `sport_name` (`sport_name`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `sports`
--

INSERT INTO `sports` (`sport_id`, `sport_name`, `description`, `rules`) VALUES
(1, 'Football', 'A team sport played between two teams of 11 players with a spherical ball.', 'Follow FIFA rules'),
(2, 'Basketball', 'A sport played by two teams of five players on a rectangular court.', 'Follow FIBA rules'),
(3, 'Volleyball', 'A game played by two teams of six players separated by a net.', 'Follow FIVB rules'),
(4, 'Tennis', 'A racket sport played individually or in doubles on a rectangular court.', 'Follow ITF rules');

-- --------------------------------------------------------

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
CREATE TABLE IF NOT EXISTS `teams` (
  `team_id` int NOT NULL AUTO_INCREMENT,
  `sport_id` int DEFAULT NULL,
  `team_name` varchar(100) NOT NULL,
  `coach_id` int DEFAULT NULL,
  `founded_year` int DEFAULT NULL,
  `home_ground` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `coach_id` (`coach_id`),
  KEY `sport_id` (`sport_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `teams`
--

INSERT INTO `teams` (`team_id`, `sport_id`, `team_name`, `coach_id`, `founded_year`, `home_ground`) VALUES
(1, 1, 'AS KIGALI', 2, 1997, 'Kigali pele Stadium'),
(2, 2, 'Ruhango Shooters', 3, 2018, 'Ruhango Arena'),
(3, 3, 'Bugesera Spikers', 11, 2016, 'Bugesera Court'),
(4, 1, 'Gasabo United', 13, 2015, 'Gasabo Stadium'),
(5, 4, 'Nyamirambo Aces', 14, 2020, 'Tennis Center Nyamirambo');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `role` enum('admin','coach','fan') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'fan',
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `full_name`, `role`, `password`, `created_at`) VALUES
(1, 'Olivier', 'GIANT OF AFRICA', 'admin', 'Olivier001', '2025-10-08 15:06:23'),
(2, 'coach_mike', 'Mike Johnson', 'coach', 'coachpass', '2025-10-08 15:06:23'),
(3, 'coach_anne', 'Anne Smith', 'coach', 'coachpass', '2025-10-08 15:06:23'),
(4, 'fan_karen', 'Karen Uwase', 'fan', '', '2025-10-08 15:06:23'),
(5, 'fan_eric', 'Eric Niyonsaba', 'fan', '', '2025-10-08 15:06:23'),
(9, 'Ben ', 'Benjamin ISHAMI', 'fan', 'Ben123', '2025-10-16 14:35:54'),
(8, 'Claudistack', 'iranzi claude', 'fan', '123456', '2025-10-15 13:52:11'),
(11, 'coach_Dior', 'Dieudonne  IRADUKUNDA', 'coach', 'coachpass', '2025-10-21 11:43:35'),
(13, 'Coach_Bahati', 'Bahati HAKIZIMANA', 'coach', 'coachpass', '2025-10-21 13:24:50'),
(14, 'coach_Adel', 'Adel Amurouchi', 'coach', 'coachpass', '2025-10-21 13:30:41'),
(15, 'iradukunda', 'dieudonne iradukunda', 'fan', '11111', '2025-10-21 14:05:03'),
(16, 'Eva', 'Evariste', 'fan', '1234', '2025-10-23 15:46:45'),
(17, 'Henry', 'Henriette M', 'fan', '12121', '2025-10-27 08:29:19'),
(18, 'Justine', 'Umwali Justine', 'fan', '12', '2025-11-05 07:31:28');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

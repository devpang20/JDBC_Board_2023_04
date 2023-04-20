# DB 생성
DROP DATABASE IF EXISTS text_board;
CREATE DATABASE text_board;

# DB 선택
USE text_board;

# 게시물 테이블 생성
CREATE TABLE article (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
	regDate DATETIME NOT NULL,
	updateDate DATETIME NOT NULL,
	title CHAR(100) NOT NULL,
	`body` TEXT NOT NULL
);

# 테스트 게시물 데이터
INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목1',
`body` = '내용1',
hit = 3;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목2',
`body` = '내용2',
hit = 7;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 2,
title = '제목3',
`body` = '내용3',
hit = 20;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 3,
title = '제목4',
`body` = '내용4',
hit = 17;

SELECT * FROM article;

# 랜덤하게 테스트 데이터 생성
INSERT INTO article (regDate, updateDate, memberId, title, `body`, hit)
SELECT NOW(), NOW(), FLOOR(RAND()*10), CONCAT('제목-', FLOOR(RAND()*100)), CONCAT('내용-', FLOOR(RAND()*100)), FLOOR(RAND()*10)
FROM article;
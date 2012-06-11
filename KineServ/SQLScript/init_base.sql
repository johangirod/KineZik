
DROP TABLE IF EXISTS EvaluatorFeedback;
DROP TABLE IF EXISTS Feedback;
DROP TABLE IF EXISTS UserSongs;
DROP TABLE IF EXISTS Evaluations;
DROP TABLE IF EXISTS AnalyzedSongs;
DROP TABLE IF EXISTS Evaluator;
DROP TABLE IF EXISTS Installation;

CREATE TABLE IF NOT EXISTS Installation(
	UUID VARCHAR(36) NOT NULL,
	PRIMARY KEY (UUID)
);



CREATE TABLE IF NOT EXISTS AnalyzedSongs(
	Id BIGINT NOT NULL AUTO_INCREMENT,
	Artist VARCHAR(100) NOT NULL,
	Album VARCHAR(100) NOT NULL,
	Title VARCHAR(100) NOT NULL,
	Genre VARCHAR(100),
	PRIMARY KEY (Id), 
	UNIQUE (Artist, Album, Title)
);

CREATE TABLE IF NOT EXISTS Evaluator(
Id INT NOT NULL AUTO_INCREMENT,
Name VARCHAR(20),
BayesianTableObject TEXT,
PRIMARY KEY(Id)
);


CREATE TABLE IF NOT EXISTS Evaluations(
	IdEval Int NOT NULL,
	IdSong BIGINT NOT NULL,
	Evaluation FLOAT NOT NULL,
	PRIMARY KEY(IdEval, IdSong),
	FOREIGN KEY(IdSong) REFERENCES AnalyzedSongs(Id),
	FOREIGN KEY(IdEval) REFERENCES Evaluator(Id),
	CONSTRAINT eval_value CHECK (Evaluation>=0 AND Evaluation<=1) 
);


CREATE TABLE IF NOT EXISTS  UserSongs(
	idUser VARCHAR(36) NOT NULL,
	idSong BIGINT NOT NULL,
	PRIMARY KEY(IdUser, IdSong),
	FOREIGN KEY(idUser) REFERENCES Installation(UUID),
	FOREIGN KEY(idSong) REFERENCES AnalyzedSongs(Id)
);

CREATE TABLE IF NOT EXISTS Feedback(
	id INT NOT NULL AUTO_INCREMENT,
	idUser VARCHAR(36) NOT NULL,
	idSong BIGINT NOT NULL,
	actionTime BIGINT NOT NULL,
	feedbackType INT NOT NULL,
	desc1 FLOAT NOT NULL,
	desc2 FLOAT NOT NULL,
	desc3 FLOAT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(idUser) REFERENCES Installation(UUID),
	FOREIGN KEY(idSong) REFERENCES AnalyzedSongs(Id)
);

CREATE TABLE IF NOT EXISTS EvaluatorFeedback(
	idFeedback INT NOT NULL,
	idEvaluator INT NOT NULL,
	FOREIGN KEY(idFeedback) REFERENCES Feedback(id),
	FOREIGN KEY(idEvaluator) REFERENCES Evaluator(Id),
	PRIMARY KEY(idFeedback,idEvaluator)
);

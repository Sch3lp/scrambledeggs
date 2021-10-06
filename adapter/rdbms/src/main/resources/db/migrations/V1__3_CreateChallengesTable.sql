CREATE TABLE CHALLENGES
(
    id uuid not null,
    challengeId varchar not null,
    challengerId uuid not null,
    opponentId uuid not null,
    comment varchar,
    appointmentSuggestion varchar,
    gameMode varchar,
    isAccepted bool
);
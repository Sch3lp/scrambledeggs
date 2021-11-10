CREATE TABLE REGISTERED_PLAYERS
(
    id uuid not null,
    nickname varchar not null,
    jwtIss varchar not null,
    jwtSub varchar not null
);

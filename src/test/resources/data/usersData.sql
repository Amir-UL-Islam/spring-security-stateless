INSERT INTO users (
    id,
    name,
    email,
    username,
    password,
    date_created,
    last_updated
) VALUES (
    1000,
    'Zed diam voluptua.',
    'Sed ut perspiciatis.',
    'admin',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    '2025-09-02 14:30:00',
    '2025-09-02 14:30:00'
);

INSERT INTO users_roles (
    users_id,
    role_id
) VALUES (
    1000,
    1100
);

INSERT INTO users (
    id,
    name,
    email,
    username,
    password,
    date_created,
    last_updated
) VALUES (
    1001,
    'At vero eos.',
    'Lorem ipsum dolor.',
    'user',
    '{bcrypt}$2a$10$FMzmOkkfbApEWxS.4XzCKOR7EbbiwzkPEyGgYh6uQiPxurkpzRMa6',
    '2025-09-03 14:30:00',
    '2025-09-03 14:30:00'
);

INSERT INTO users_roles (
    users_id,
    role_id
) VALUES (
    1001,
    1101
);

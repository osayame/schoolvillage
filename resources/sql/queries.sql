-- name: select-all-users
SELECT * from users

-- name: select-user
-- Returns the flag for a single company
select * from users
where id = :id

-- name: insert-user<!
-- Inserts a single user
INSERT INTO users (
  id,
  first_name,
  last_name,
  email,
  phone,
  status,
  photo,
  resume,
  address1,
  city,
  state,
  zip,
  url,
  acuity_id,
  created_at)
VALUES (
  nextval('users_serial'),
  :first_name,
  :last_name,
  :email,
  :phone,
  'Pending'::user_status,
  :photo,
  :resume,
  :address1,
  :city,
  :state,
  :zip,
  :url,
  :acuity_id,
  CURRENT_TIMESTAMP)

-- name: update-user<!
-- Updates a single user
UPDATE users SET
first_name = :first_name,
last_name = :last_name,
email = :email,
phone = :phone,
photo = :photo,
resume = :resume,
address1 = :address1,
city = :city,
state = :state,
zip = :zip,
url = :url,
acuity_id = :acuity_id,
updated_at = CURRENT_TIMESTAMP
WHERE id = :id


-- name: select-flagged-users
SELECT * from users where status = 'Flagged'
ORDER BY (CASE WHEN updated_at IS NULL THEN 0 ELSE 1 END) DESC,
         updated_at DESC

-- name: select-pending-users
SELECT * from users where status = 'Pending'
ORDER BY (CASE WHEN updated_at IS NULL THEN 0 ELSE 1 END) DESC,
         updated_at DESC

-- name: select-approved-users
SELECT * from users
where status = 'Approved'

-- name: select-recent-users
SELECT * from users
where status = 'Approved' AND updated_at IS NOT NULL
ORDER BY updated_at DESC

-- name: update-status<!
-- Changes the status of a single user
update users
set status = :status::user_status, updated_at = CURRENT_TIMESTAMP
where id = :id


---


-- name: all-subsidiaries
SELECT subsidiaries from users

-- name: users-and-urls
SELECT id, lower(url) as url from customerinfo WHERE url is not null

-- name: try-login
SELECT * from customerinfo WHERE username = :username AND password = :password

-- name: find-dbadmin
SELECT * from dbAdminInfo WHERE username = :username AND password = :password

-- name: all-subsidiaries
SELECT * from subsidiaries

-- name: find-subject
-- Returns all matching parent companies
SELECT * from users
where (select cast(:subject as text)) = ANY (subjects)

-- name: find-subsidiaries
-- Returns a single company's subsidiaries
SELECT subsidiaries
FROM users
WHERE id = :id;


-- name: insert-subsidiary<!
-- Inserts a single subsidiary
update users
set subsidiaries = array_append(subsidiaries, (select cast(:subsidiary as text))), updated_at = CURRENT_TIMESTAMP
where id = :id

-- name: update-subsidiary<!
-- Updates a single subsidiary
update users
set subsidiaries = array_replace(subsidiaries,(select cast(:old as text)), (select cast(:new as text))), updated_at = CURRENT_TIMESTAMP
where id = :id

-- name: delete-subsidiary<!
-- Deletes a single subsidiary
update users
set subsidiaries = array_remove(subsidiaries, (select cast(:subsidiary as text))), updated_at = CURRENT_TIMESTAMP
where id = :id

-- name: add-flag<!
-- Flags a single company
update users
set status = 'Flagged', flag_comment = :comment, updated_at = CURRENT_TIMESTAMP
where id = :id

-- name: remove-flag<!
-- Removes a flag from a single company
update users
set status = 'Pending', updated_at = CURRENT_TIMESTAMP
where id = :id



-- name: get-flag-comment
-- Returns the flag comment for a single company
select flag_comment from users
where id = :id

-- name: get-flag-status
-- Returns the flag for a single company
select status from users
where id = :id

-- name: get-all-versions-count
-- Returns the number of versions
select cast(count(*) as integer) from companyversions

-- name: insert-version-without-data<!
-- Inserts a new version of a company
insert into companyversions (data, company_id, created_at)
values ((select row_to_json(users) from users where id = :id)::jsonb, :id, CURRENT_TIMESTAMP)

-- name: insert-version-with-data<!
-- Inserts a new version of a company
insert into companyversions (data, company_id, created_at)
values ((SELECT :data::JSONB), :id, CURRENT_TIMESTAMP)


-- name: get-recent-version
-- Returns the most recent version of a company
select * from companyversions where company_id = :id ORDER BY created_at DESC LIMIT 1


-- name: insert-dbadmin-user<!
-- Inserts a new user into the Users table
-- Expects :username, :email, and :password
INSERT INTO users (username, password)
VALUES (:username, :password)

-- name: get-user-by-username
-- Fetches a user from the DB based on username.
-- Expects :username
SELECT *
FROM users
WHERE username=:username

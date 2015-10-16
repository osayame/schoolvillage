-- name: select-all-users
SELECT * from users

-- name: select-user
-- Returns the flag for a single company
select * from users
where id = :id

-- name: update-user<!
-- Updates a single user
UPDATE users SET
first_name = :first_name,
last_name = :last_name,
email = :email,
phone = :phone,
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

-- name: select-recent-users
SELECT * from users
where status = 'Approved' AND updated_at IS NOT NULL
ORDER BY updated_at DESC



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

-- name: insert-company<!
-- Inserts a single company
INSERT INTO users (
id,
first_name,
last_name,
region,
corporate_contact_name,
email,
phone,
matching_gift_offered,
retirees_eligible,
part_time_eligible,
full_time_eligible,
maximum_matched,
minimum_matched,
ratio,
matching_gift_process,
url_forms,
url_guidelines,
intranet_forms,
intranet_guidelines,
volunteer_grant_offered,
volunteer_grant_process,
volunteer_minimum_hours_required,
corporate_donation_offered,
higher_ed,
k12,
arts_and_cultural,
environmental,
health_human_services,
civic_community,
subsidiaries,
status,
updated_at)
VALUES (
nextval('companies_serial'),
:first_name,
:last_name,
:region,
:corporate_contact_name,
:email,
:phone,
:matching_gift_offered,
:retirees_eligible,
:part_time_eligible,
:full_time_eligible,
:maximum_matched,
:minimum_matched,
:ratio,
:matching_gift_process,
:url_forms,
:url_guidelines,
:intranet_forms,
:intranet_guidelines,
:volunteer_grant_offered,
:volunteer_grant_process,
:volunteer_minimum_hours_required,
:corporate_donation_offered,
:higher_ed,
:k12,
:arts_and_cultural,
:environmental,
:health_human_services,
:civic_community,
:subsidiaries,
'Approved',
CURRENT_TIMESTAMP)

-- name: insert-user<!
-- Inserts a single user
INSERT INTO users (
id,
first_name,
last_name,
email,
phone,
status,
created_at)
VALUES (
nextval('users_serial'),
:first_name,
:last_name,
:email,
:phone,
'Pending',
CURRENT_TIMESTAMP)


-- name: approve-version<!
-- Replaces a copy with a specified version
UPDATE users
set
first_name = data->>'first_name',
last_name = data->>'last_name',
corporate_contact_name = data->>'corporate_contact_name',
email = data->>'email',
phone=data->>'phone',
matching_gift_offered = (select cast(t.data->>'matching_gift_offered' as boolean)),
retirees_eligible = (select cast(t.data->>'retirees_eligible' as boolean)),
part_time_eligible = (select cast(t.data->>'part_time_eligible' as boolean)),
full_time_eligible = (select cast(t.data->>'full_time_eligible' as boolean)),
maximum_matched = t.data->>'maximum_matched',
minimum_matched = t.data->>'minimum_matched',
ratio = data->>'ratio',
matching_gift_process = data->>'matching_gift_process',
url_forms = data->>'url_forms',
url_guidelines = data->>'url_guidelines',
volunteer_grant_offered = (select cast(t.data->>'volunteer_grant_offered' as boolean)),
volunteer_grant_process = data->>'volunteer_grant_process',
volunteer_minimum_hours_required = data->>'volunteer_minimum_hours_required',
intranet_forms = (select cast(t.data->>'intranet_forms' as boolean)),
intranet_guidelines = (select cast(t.data->>'intranet_guidelines' as boolean)),
higher_ed = (select cast(t.data->>'higher_ed' as boolean)),
k12 = (select cast(t.data->>'k12' as boolean)),
arts_and_cultural = (select cast(t.data->>'arts_and_cultural' as boolean)),
environmental = (select cast(t.data->>'environmental' as boolean)),
health_human_services = (select cast(t.data->>'health_human_services' as boolean)),
civic_community = (select cast(t.data->>'civic_community' as boolean)),
updated_at = CURRENT_TIMESTAMP,
status = 'Approved'
from (select company_id, data from companyversions where id = :version) as t
where id = t.company_id

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


-- name: change-status<!
-- Changes the status of a single company
update users
set status = :status::company_status, updated_at = CURRENT_TIMESTAMP
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

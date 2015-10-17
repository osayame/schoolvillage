ALTER TABLE users ADD COLUMN status user_status;
--;;

update users set status = 'Approved';

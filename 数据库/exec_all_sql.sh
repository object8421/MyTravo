echo begin exec
user=root
pass=123456
mysql -u $user --password=$pass travo < ./triggers.sql
mysql -u $user --password=$pass travo < ./proc_default.sql
mysql -u $user --password=$pass travo < ./proc_location.sql
mysql -u $user --password=$pass travo < ./proc_sync.sql
mysql -u $user --password=$pass travo < ./proc_user.sql
mysql -u $user --password=$pass travo < ./proc_travel.sql
mysql -u $user --password=$pass travo < ./proc_note.sql
echo sucess 

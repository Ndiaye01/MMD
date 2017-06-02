DROP TABLE IMAGE;

CREATE  TABLE IMAGE (
img_id NUMBER(20),
img BLOB );

INSERT INTO IMAGE (img_id,img)

VALUES (1, utl_raw.cast_to_raw('/Users/teaching-hiwi/Me.jpg'));

SELECT * FROM IMAGE;
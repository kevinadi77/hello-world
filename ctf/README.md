Capture the Flag Challenge
==========================

To install
----------

Run mongod with --auth

Ensure there is an 'madmin' user:

	db.getSiblingDB("admin").createUser({
	       user: "madmin",
	       pwd: "mongoctf",
	       roles: [ "root" ]
	     });


Run the script:

	mongo admin -u madmin -p mongoctf ctf_setup.js


Upon successful completion, have users connect to the first challenge:

	mongo <HOSTNAME>/level01 -u level01 -p level01

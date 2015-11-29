echo $# $1 $2
/opt/glassfish4/glassfish/bin/asadmin --user admin \
	--passwordfile /opt/glassfish4/passfile deploy --contextroot $2 \
	--force=true --name $3 --target server ./target/$1


LOOPS=$1

echo "Generating $LOOPS blocks";

for ((n=0;n<$LOOPS;n++)); do
    NUMBER=$(( ( RANDOM % 100000 )  + 1 ))

    cat > /contract.rho << EOL
contract @"add_${NUMBER}"(@number, cb) = {
    cb!(number + ${NUMBER})
}
|
new foo in {
    @"add_${NUMBER}"!(42, *foo)
}
EOL

    /opt/docker/bin/rnode deploy  --phlo-limit 1000 --phlo-price 1 /contract.rho
    /opt/docker/bin/rnode propose
done

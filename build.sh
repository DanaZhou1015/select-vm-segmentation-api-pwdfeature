#!/bin/bash
RUN_SH_NAME=acpbe
TOPDIR=`pwd`
# ..........
SH_NAME=ams-acp-be
# .........
TMPDIR=/tmp
# .pom.xml......
NAME=acp-be
VERSION=`get_version acp_be ams`
FINALNAME=audience-curation
MODULE=$NAME
# git version from gitlog
GITVERSION=`git log | grep commit | awk 'NR==1{print $2}'`
GITLOG=`echo $GITVERSION | cut -c1-8`
# docker service whether start
if [[ ! -z `docker version 2>&1 | grep "Cannot connect to the Docker daemon"`  ]]
then
    echo docker service is not running, please start docker with \`service docker start\` command
    exit 0
fi
rm -rf $TMPDIR/$NAME
mkdir -p $TMPDIR/$NAME
# install.sh
read -r -d "" INSTALL_SH << EOM
#!/bin/bash
MODULE=$NAME
# bin............
tmp=/tmp/\$MODULE
dir_tmp=/opt/\$MODULE
log_tmp=/var/log/\$MODULE
package_name=\$MODULE.tar.gz
IMAGE_PACKAGE=\$MODULE-image.tar
VERSION=$VERSION
NAME=\$MODULE
FOLDER=\$MODULE
rm \$tmp -rf
mkdir \$tmp
# bin...
sed -n -e '1,/^exit 0$/!p' \$0 > "\${tmp}/\${package_name}" 2>/dev/null
cd \$tmp
tar zxf \$package_name
# application.properties
if [ ! -d \$dir_tmp ]
then
    mkdir -p \$dir_tmp
    mkdir -p \$dir_tmp/tmp
    cp conf \$dir_tmp/ -R
	cp crt \$dir_tmp/ -R
fi
# log
if [ ! -d \$log_tmp ]
then
    mkdir -p \$log_tmp
fi
cp $RUN_SH_NAME.sh \$dir_tmp
# Stop docker.
docker ps | grep "\b$NAME\b" | awk '{print \$1}' | xargs docker stop || true
# Remove docker image
docker images | grep "\b\$NAME\b" | awk -v img_name="\$NAME" '{print img_name":"\$2}' | xargs docker rmi -f || true
# Load docker
INFO=\`docker load --input \$tmp/\$IMAGE_PACKAGE\`
NAME=\`echo \$INFO | cut -d\: -f2 | tr -d ''\`
VERSION=\`echo \$INFO | cut -d\: -f3 | tr -d ''\`
if [ -f /usr/bin/$SH_NAME ]
then
    rm /usr/bin/$SH_NAME -f
fi
# soft link.......
ln -s \$dir_tmp/$RUN_SH_NAME.sh /usr/bin/$SH_NAME
chmod 755 /usr/bin/$SH_NAME
    
echo install successfully to \$dir_tmp, you can run $SH_NAME command
exit 0
EOM
echo "${INSTALL_SH}" > $TMPDIR/$NAME/install-$GITLOG.sh
# run.sh .....
read -r -d "" RUN_SH << EOM
#!/bin/bash
NAME=@
VERSION=@
dir_tmp=/opt/\$NAME
log_tmp=/var/log/\$NAME
IMG_LIST=\`docker ps | grep "\b\$NAME:\$VERSION\b" | awk 'NR>=1{print \$1}'\`
case "\$1" in
        -version)
        echo Current \$NAME version is \$VERSION
        echo Build from git commit log: $GITVERSION
        exit 0
        ;;
        # restart the docker container
        -restart)
        if [[ ! -z IMG_LIST ]]
        then
                echo Restarting docker image
                echo \$IMG_LIST | xargs docker restart || true
        else
                echo No docker images is running,please start the image first.
        fi
        exit 0
        ;;
        -help)
        cat << EOF
Usage: sh $RUN_SH_NAME.sh [-options]
where options include:
        -start         Start the Taxonomy API service
        -stop          Stop the Taxonomy API service
        -restart       Restart the Taxonomy API service
        -version       print product version
        -help          print this help message
EOF
        exit 0
        ;;
        -stop)
        if [[ ! -z IMG_LIST ]]
        then
                echo Stopping docker image \$NAME:\$VERSION
                echo \$IMG_LIST | xargs docker stop || true
        else
                echo No docker images is running,please start the image first.
        fi
        exit 0
        ;;
        -start)
        if [[ -z \$2 ]]
        then
          PORT='8080'
        else
          PORT=\$2
        fi
        echo Starting docker image \$NAME:\$VERSION
		docker run -itd -p \$PORT:8080 \
		-v \$dir_tmp/conf/application.properties:/opt/$NAME/application.properties \
		-v /data/smartaudience-data/:/opt/$NAME/audience_data/ \
		-v \$dir_tmp/tmp/:/tmp/$NAME/ \
		-v \$dir_tmp/crt/:/opt/acp-be/crt/ \
		-v \$log_tmp/:/tmp/audience-curation-be/ \
		-v /etc/localtime:/etc/localtime \
		--privileged=true \$NAME:\$VERSION
        exit 0
        ;;
        -startwithbeats)
         if [[ -z \$2 ]]
        then
          PORT='8080'
        else
          PORT=\$2
        fi
        echo Starting docker image \$NAME:\$VERSION with beats package
		docker run -itd -p \$PORT:8080 \
		-v \$dir_tmp/conf/application.properties:/opt/$NAME/application.properties \
		-v /data/smartaudience-data/:/opt/$NAME/audience_data/ \
		-v \$dir_tmp/tmp/:/tmp/$NAME/ \
		-v \$dir_tmp/crt/:/opt/acp-be/crt/ \
		-v \$log_tmp/:/tmp/audience-curation-be/ \
		-v \$dir_tmp/conf/filebeat.yml:/opt/filebeat/filebeat.yml \
		-v /etc/localtime:/etc/localtime \
		--privileged=true \$NAME:\$VERSION
		NEW_IMAGE=\`docker ps | grep "\b\$NAME:\$VERSION\b" | awk 'NR>=1{print \$1}'\`
        docker exec -itd \$NEW_IMAGE bash -c "/opt/filebeat/filebeat -e -c /opt/filebeat/filebeat.yml"
        exit 0
        ;;
	--*|-*|*)
        echo Unknown option, plase checkout arguments list with -help option
        exit 0
        ;;
esac
echo Unknown option, plase checkout arguments list with -help option
exit 0
EOM
echo "$RUN_SH" > $TMPDIR/$NAME/run-$GITLOG.sh
# maven package
mvn -U clean compile package
if [ ! -z $FINALNAME ]
then
    echo "finalname is not empty"
    cp $TOPDIR/target/$FINALNAME-* $TMPDIR/$NAME
elif [ -f "$TOPDIR/target/$NAME*" ]
then
    echo "package with version number"
    cp $TOPDIR/target/$NAME* $TMPDIR/$NAME
else
    echo "cannot find any package at $TOPDIR/target"
fi
mkdir -p $TMPDIR/$NAME
mkdir -p $TMPDIR/$NAME/conf
cp $TOPDIR/src/main/resources/application.properties $TMPDIR/$NAME/conf/application.properties
cp $TOPDIR/init/tmp/CampaignTemplate.xlsx $TMPDIR/$NAME/conf/CampaignTemplate.xlsx
cp $TOPDIR/src/main/resources/beats/filebeat.yml $TMPDIR/$NAME/conf/filebeat.yml
# copy Dockerfile for docker image
cp $TOPDIR/Dockerfile $TMPDIR/$NAME
mkdir -p $TMPDIR/$NAME/crt
cp $TOPDIR/qacas.crt $TMPDIR/$NAME/crt/
cp $TOPDIR/amsdomain.crt $TMPDIR/$NAME/crt/
# Stop docker and remove image
docker ps | grep "\b$NAME\b" | awk '{print $1}' | xargs docker stop || true
docker images | grep "\b$NAME\b" | awk -v img_name="$NAME" '{print img_name":"$2}' | xargs docker rmi -f || true
# Build docker image
docker build -t $NAME:$VERSION $TMPDIR/$NAME
# Save docker image
docker save $NAME:$VERSION > $TMPDIR/$NAME/$NAME-image.tar
cat $TMPDIR/$NAME/run-$GITLOG.sh | sed "s/NAME=@/NAME=$NAME/g" | sed "s/VERSION=@/VERSION=$VERSION/g" > $TMPDIR/$NAME/$RUN_SH_NAME.sh
cd $TMPDIR/$NAME
tar zcf $NAME.tar.gz crt conf $NAME-image.tar  $RUN_SH_NAME.sh
# bin package
cat install-$GITLOG.sh $NAME.tar.gz > $NAME-$VERSION-$GITLOG.bin
echo bin file $NAME-$VERSION-$GITLOG.bin build success, now you can run install file
exit 0
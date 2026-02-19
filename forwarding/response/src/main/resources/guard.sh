#!/bin/bash
set -e

DATE=$(date +%Y%m%d%H%M)
# sh guard.sh -s response -d ./ -t 1
# sh /root/fill/server/guard.sh -s filling-gateway -d /icss/project/fill/ -t 1
# sh /root/fill/server/guard.sh -s filling-module-report-biz -d /icss/project/fill/ -t 1
# sh /root/fill/server/guard.sh -s filling-module-poi-biz -d /icss/project/fill/ -t 1
# sh /root/fill/server/guard.sh -s filling-module-system-biz -d /icss/project/fill/ -t 1
# sh /root/fill/server/guard.sh -s filling-module-bpm-biz -d /icss/project/fill/ -t 1
# sh /root/fill/server/guard.sh -s filling-module-infra-biz -d /icss/project/fill/ -t 1

# 脚本路径
BASE_PATH=$(cd $(dirname $0) && pwd)
# 部署路径
DEPLOY_PATH=${BASE_PATH}/target
# 服务名称
SERVER_NAME="NULL"
# 选择类型 0:检查服务 1:检查和启动服务 2:查看日志 10:停止服务
OPTION_TYPE="0"
JAVA_CMD="java"

# JVM 参数
#GC="-XX:+UseBiasedLocking -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:NewRatio=2 -XX:+CMSIncrementalMode -XX:-ReduceInitialCardMarks -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly"
#EX="-XX:+OptimizeStringConcat -XX:+DoEscapeAnalysis -XX:+UseNUMA"
#HEAP=" -Xms1024M -Xmx2048M -XX:CompressedClassSpaceSize=512m -XX:MetaspaceSize=800m -XX:MaxMetaspaceSize=800m -XX:MaxDirectMemorySize=512m"
#JAVA_OPTIONS="${GC} ${EX} ${HEAP}"
JAVA_OPTIONS=""

ACTIVE="dev"

while getopts ":s:d:t:j:" opt
do
    case $opt in
        s)
        SERVER_NAME=${OPTARG}
        ;;
        d)
        DEPLOY_PATH=${OPTARG}
        ;;
        t)
        OPTION_TYPE=${OPTARG}
        ;;
        j)
        JAVA_CMD=$OPTARG
        ;;
        ?)
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") unknown shell params $OPTARG"
        ;;
    esac
done

# 查看服务日志
function view_log() {
    server_name=$1
    cd ${DEPLOY_PATH}
    tail -f ${server_name}/${server_name}.jar.log
}

# 启动单个服务
function start_server() {
    server_name=$1
    cd ${DEPLOY_PATH}
    # 开启启动前，打印启动参数
    echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [start] 开始启动 ${server_name}"
    echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [start] JAVA_OPS: $JAVA_OPTIONS"
    # 开始启动
    BUILD_ID=dontKillMe nohup ${JAVA_CMD} -Dfile.encoding=utf-8  -server $JAVA_OPTIONS -jar ${server_name} --spring.profiles.active=${ACTIVE} > ${server_name}.log 2>&1 &
    echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [start] 启动 ${server_name} 完成"
}

# 停止单个服务
function stop_server() {
    jar_name=$1
    echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [stop] stop ${jar_name}"
    PID=$(ps -ef | grep ${jar_name}.jar | grep -v "grep" | awk '{print $2}')
    if [ -n "$PID" ]; then
        # 正常关闭
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [stop] ${jar_name} is running, kill [$PID]"
        kill -9 $PID
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [stop] stop ${jar_name} success"
#        kill -15 $PID
#        # 等待最大 30 秒，直到关闭完成。
#        for ((i = 0; i < 30; i++))
#            do
#                sleep 1
#                PID=$(ps -ef | grep ${jar_name}.jar | grep -v "grep" | awk '{print $2}')
#                if [ -n "$PID" ]; then
#                    echo -e ".\c"
#                else
#                    echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [stop] stop ${jar_name} success"
#                    break
#                fi
#		    done
#
#        # 如果正常关闭失败，那么进行强制 kill -9 进行关闭
#        if [ -n "$PID" ]; then
#            echo "[stop] $jar_name failed，force kill -9 $PID"
#            kill -9 $PID
#        fi
    # 如果 Java 服务未启动，则无需关闭
    else
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") [stop] $jar_name is not run，not need stop"
    fi
}

#检查服务 1:run 0:not run
function check_server() {
    server_name=$1
    exec_stop=$2
    PID=$(ps -ef | grep ${server_name}.jar | grep -v "grep" | awk '{print $2}')
    #centos 使用这个
    if [ -n "$PID" ]; then
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") ${server_name} 运行中，PID [${PID}] RES [1]"
        if [ ${exec_stop} == 1 ]; then
            exit 1
        fi
    else
        echo "[cmcc]-$(date "+%Y.%m.%d-%H.%M.%S") ${server_name} 未启动, RES [0]}]"
    fi
    return 0
}



# 检查和启动服务
function check_start() {
    jar_name=$1
    check_server ${jar_name} 1
    jar_file=${jar_name}.jar
    jar_name=$(echo ${jar_file} | awk -F "/"  '{print $NF}'  | awk -F ".jar" '{print $1}')
    start_server ${jar_name}/${jar_file}
    temp_res=$?
    if [ ${temp_res} = 0 ]; then
        return 0
    fi
}

if [ "$OPTION_TYPE" = "0" ]; then
    check_server ${SERVER_NAME} 0
elif [ "$OPTION_TYPE" = "1" ]; then
    check_start ${SERVER_NAME}
elif [ "$OPTION_TYPE" = "2" ]; then
    view_log ${SERVER_NAME}
elif [ "$OPTION_TYPE" = "10"  ]; then
    stop_server ${SERVER_NAME}
fi



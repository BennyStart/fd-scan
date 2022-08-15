FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jeecg-cloud-system-biz

WORKDIR /jeecg-cloud-system-biz

EXPOSE 8001

ADD ./target/jeecg-cloud-system-biz-2.2.0.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar jeecg-cloud-system-biz-2.2.0.jar

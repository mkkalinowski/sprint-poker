FROM pandeiro/lein

RUN apt-get update \
 && apt-get autoremove \
 && apt-get install -y git

WORKDIR /root
RUN git clone https://github.com/tokenshift/sprint-poker.git

WORKDIR /root/sprint-poker
RUN lein compile

EXPOSE 3000
CMD lein run

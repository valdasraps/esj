#!/bin/bash

CD=`dirname $0` || { echo Failed to get current dir; exit 1; }

function clean {
  rm -fR $CD/net $CD/ClientMessageDtos*
}

function getproto {
  wget -P "$CD" https://raw.github.com/EventStore/EventStore/master/src/EventStore/Protos/ClientAPI/ClientMessageDtos.proto
  sed -i.bak 's/package EventStore\.Client\.Messages\;/package net.eventstore.client.message;/' "$CD/ClientMessageDtos.proto"
}

function process {
  protoc --java_out="$CD" ClientMessageDtos.proto
}

function copyjava {
  cp $CD/net/eventstore/client/message/ClientMessageDtos.java $CD/../main/java/net/eventstore/client/message
}

clean
getproto
process
copyjava
clean

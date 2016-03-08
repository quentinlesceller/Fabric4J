#!/bin/bash
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/chaincode.proto
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/openchain.proto
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/api.proto
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/devops.proto
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/events.proto
protoc -I=src/main/resources/proto --java_out=src/main/java src/main/resources/proto/server_admin.proto
exit 0

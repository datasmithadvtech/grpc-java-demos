/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.springboot;

import com.example.echo.EchoOuterClass;
import com.example.echo.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.autoconfigure.grpc.server.GrpcService;

/**
 * Created by rayt on 5/18/16.
 */
@GrpcService(EchoServiceGrpc.class)
public class EchoServiceImpl implements EchoServiceGrpc.EchoService {
  @Override
  public void echo(EchoOuterClass.Echo request, StreamObserver<EchoOuterClass.Echo> responseObserver) {
    System.out.println(request);
    responseObserver.onNext(request);
    responseObserver.onCompleted();
  }
}

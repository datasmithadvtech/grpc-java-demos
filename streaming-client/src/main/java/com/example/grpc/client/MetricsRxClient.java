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

package com.example.grpc.client;

import com.example.server.streaming.MetricsServiceGrpc;
import com.example.server.streaming.StreamingExample;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rx.Observable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsRxClient {
  private final Channel channel;
  private final MetricsServiceGrpc.MetricsServiceStub stub;

  MetricsRxClient(Channel channel) {
    this.channel = channel;
    this.stub = MetricsServiceGrpc.newStub(channel);
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext(true).build();

    rx.Observable<Long> metrics = rx.Observable.from(new Long[] {1L, 2L, 3L, 4L});

    MetricsRxClient client = new MetricsRxClient(channel);

    Future<StreamingExample.Average> future = client.collect(metrics.map(l -> StreamingExample.Metric.newBuilder().setMetric(l).build()));

    System.out.println("Average: " + future.get());

    channel.shutdownNow();
  }

  public Future<StreamingExample.Average> collect(Observable<StreamingExample.Metric> metrics) {
    CompletableFuture<StreamingExample.Average> future = new CompletableFuture<>();
    StreamObserver<StreamingExample.Metric> collector = stub.collect(new StreamObserver<StreamingExample.Average>() {
      @Override
      public void onNext(StreamingExample.Average value) {
        future.complete(value);
      }

      @Override
      public void onError(Throwable t) {
        future.completeExceptionally(t);
      }

      @Override
      public void onCompleted() {
      }
    });
    metrics.doOnCompleted(collector::onCompleted).forEach(collector::onNext);
    return future;
  }
}

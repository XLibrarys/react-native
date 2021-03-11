/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * <p>This source code is licensed under the MIT license found in the LICENSE file in the root
 * directory of this source tree.
 */
package com.facebook.react.bridge.queue;

import android.os.Looper;
import com.facebook.react.common.MapBuilder;
import java.util.Map;

public class ReactQueueConfigurationImpl implements ReactQueueConfiguration {
  //UI消息线程
  private final MessageQueueThreadImpl mUIQueueThread;
  //NativeModule消息线程
  private final MessageQueueThreadImpl mNativeModulesQueueThread;
  //JS消息线程
  private final MessageQueueThreadImpl mJSQueueThread;

  private ReactQueueConfigurationImpl(
      MessageQueueThreadImpl uiQueueThread,
      MessageQueueThreadImpl nativeModulesQueueThread,
      MessageQueueThreadImpl jsQueueThread) {
    mUIQueueThread = uiQueueThread;
    mNativeModulesQueueThread = nativeModulesQueueThread;
    mJSQueueThread = jsQueueThread;
  }

  @Override
  public MessageQueueThread getUIQueueThread() {
    return mUIQueueThread;
  }

  @Override
  public MessageQueueThread getNativeModulesQueueThread() {
    return mNativeModulesQueueThread;
  }

  @Override
  public MessageQueueThread getJSQueueThread() {
    return mJSQueueThread;
  }

  /**
   * Should be called when the corresponding {@link com.facebook.react.bridge.CatalystInstance} is
   * destroyed so that we shut down the proper queue threads.
   */
  public void destroy() {
    if (mNativeModulesQueueThread.getLooper() != Looper.getMainLooper()) {
      mNativeModulesQueueThread.quitSynchronous();
    }
    if (mJSQueueThread.getLooper() != Looper.getMainLooper()) {
      mJSQueueThread.quitSynchronous();
    }
  }

  public static ReactQueueConfigurationImpl create(
      ReactQueueConfigurationSpec spec, QueueThreadExceptionHandler exceptionHandler) {
    Map<MessageQueueThreadSpec, MessageQueueThreadImpl> specsToThreads = MapBuilder.newHashMap();

    //创建UI线程消息队列
    MessageQueueThreadSpec uiThreadSpec = MessageQueueThreadSpec.mainThreadSpec();
    MessageQueueThreadImpl uiThread = MessageQueueThreadImpl.create(uiThreadSpec, exceptionHandler);
    specsToThreads.put(uiThreadSpec, uiThread);

    //创建JS线程消息队列
    MessageQueueThreadImpl jsThread = specsToThreads.get(spec.getJSQueueThreadSpec());
    if (jsThread == null) {
      jsThread = MessageQueueThreadImpl.create(spec.getJSQueueThreadSpec(), exceptionHandler);
    }

    //创建Native线程消息队列
    MessageQueueThreadImpl nativeModulesThread =
        specsToThreads.get(spec.getNativeModulesQueueThreadSpec());
    if (nativeModulesThread == null) {
      nativeModulesThread =
          MessageQueueThreadImpl.create(spec.getNativeModulesQueueThreadSpec(), exceptionHandler);
    }

    return new ReactQueueConfigurationImpl(uiThread, nativeModulesThread, jsThread);
  }
}

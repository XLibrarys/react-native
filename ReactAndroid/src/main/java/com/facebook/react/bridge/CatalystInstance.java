/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * <p>This source code is licensed under the MIT license found in the LICENSE file in the root
 * directory of this source tree.
 */
package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.turbomodule.core.interfaces.JSCallInvokerHolder;
import java.util.Collection;
import java.util.List;

/**
 * 在异步JSC bridge上的更高级的API。这提供了一个允许调用JavaScript方法的环境，也允许从JavaScript调用一组Java api。
 */
@DoNotStrip
public interface CatalystInstance
    extends MemoryPressureListener, JSInstance, JSBundleLoaderDelegate {
  void runJSBundle();

  // 返回运行JS包的状态;如果runJSBundle正在运行，则等待答案
  boolean hasRunJSBundle();

  /**
   * 返回运行的JS包的源URL，如果没有运行JS包，返回{@code null}。
   */
  @Nullable
  String getSourceURL();

  // This is called from java code, so it won't be stripped anyway, but proguard will rename it,
  // which this prevents.
  @Override
  @DoNotStrip
  void invokeCallback(int callbackID, NativeArrayInterface arguments);

  @DoNotStrip
  void callFunction(String module, String method, NativeArray arguments);

  /**
   * 销毁这个catalyst实例，等待ReactQueueConfiguration中的其他线程(UI线程之外)完成运行。必须从UI线程调用，以便
   * 我们可以完全关闭其他线程。
   */
  void destroy();

  boolean isDestroyed();

  /** 初始化所有的native modules */
  @VisibleForTesting
  void initialize();

  ReactQueueConfiguration getReactQueueConfiguration();

  <T extends JavaScriptModule> T getJSModule(Class<T> jsInterface);

  <T extends NativeModule> boolean hasNativeModule(Class<T> nativeModuleInterface);

  <T extends NativeModule> T getNativeModule(Class<T> nativeModuleInterface);

  NativeModule getNativeModule(String moduleName);

  JSIModule getJSIModule(JSIModuleType moduleType);

  Collection<NativeModule> getNativeModules();

  /**
   * This method permits a CatalystInstance to extend the known Native modules. This provided
   * registry contains only the new modules to load.
   */
  void extendNativeModules(NativeModuleRegistry modules);

  /**
   * Adds a idle listener for this Catalyst instance. The listener will receive notifications
   * whenever the bridge transitions from idle to busy and vice-versa, where the busy state is
   * defined as there being some non-zero number of calls to JS that haven't resolved via a
   * onBatchCompleted call. The listener should be purely passive and not affect application logic.
   */
  void addBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener);

  /**
   * Removes a NotThreadSafeBridgeIdleDebugListener previously added with {@link
   * #addBridgeIdleDebugListener}
   */
  void removeBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener);

  /** This method registers the file path of an additional JS segment by its ID. */
  void registerSegment(int segmentId, String path);

  @VisibleForTesting
  void setGlobalVariable(String propName, String jsonValue);

  /**
   * Get the C pointer (as a long) to the JavaScriptCore context associated with this instance.
   *
   * <p>Use the following pattern to ensure that the JS context is not cleared while you are using
   * it: JavaScriptContextHolder jsContext = reactContext.getJavaScriptContextHolder()
   * synchronized(jsContext) { nativeThingNeedingJsContext(jsContext.get()); }
   */
  JavaScriptContextHolder getJavaScriptContextHolder();

  void addJSIModules(List<JSIModuleSpec> jsiModules);

  /**
   * Returns a hybrid object that contains a pointer to JSCallInvoker. Required for
   * TurboModuleManager initialization.
   */
  JSCallInvokerHolder getJSCallInvokerHolder();

  /**
   * For the time being, we want code relying on the old infra to also work with TurboModules.
   * Hence, we must provide the TurboModuleRegistry to CatalystInstance so that getNativeModule,
   * hasNativeModule, and getNativeModules can also return TurboModules.
   */
  void setTurboModuleManager(JSIModule getter);
}

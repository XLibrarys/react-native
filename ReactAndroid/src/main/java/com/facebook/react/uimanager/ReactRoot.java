/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * <p>This source code is licensed under the MIT license found in the LICENSE file in the root
 * directory of this source tree.
 */
package com.facebook.react.uimanager;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.facebook.react.uimanager.common.UIManagerType;

/** 用于React Native程序的根native view的接口*/
public interface ReactRoot {

  /** Return cached launch properties for app */
  @Nullable
  Bundle getAppProperties();

  @Nullable
  String getInitialUITemplate();

  String getJSModuleName();

  /** Fabric or Default UI Manager, see {@link UIManagerType} */
  @UIManagerType
  int getUIManagerType();

  int getRootViewTag();

  void setRootViewTag(int rootViewTag);

  /** 调用JS来启动React应用程序。*/
  void runApplication();

  /** Handler for stages {@link com.facebook.react.surface.ReactStage} */
  void onStage(int stage);

  /** Return native view for root */
  ViewGroup getRootViewGroup();

  /** @return Cached values for widthMeasureSpec and heightMeasureSpec */
  int getWidthMeasureSpec();

  int getHeightMeasureSpec();

  /** Sets a flag that determines whether to log that content appeared on next view added. */
  void setShouldLogContentAppeared(boolean shouldLogContentAppeared);
}

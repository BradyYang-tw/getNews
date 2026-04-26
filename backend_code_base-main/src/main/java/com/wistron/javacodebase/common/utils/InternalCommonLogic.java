package com.wistron.javacodebase.common.utils;

import com.wistron.javacodebase.common.utils.contracts.IInternalCommonVO;

public class InternalCommonLogic {

  private final IInternalCommonVO commonVO;

  // Constructor to initialize the commonVO
  public InternalCommonLogic(IInternalCommonVO commonVO) {
    this.commonVO = commonVO;
  }

  /**
   * Returns the unique identifier of the commonVO.
   *
   * @return the unique identifier as a String
   */
  public String getCommonVOId() {
    if (commonVO != null) {
      return commonVO.getId();
    } else {
      return null; // or throw an exception if preferred
    }
  }
}

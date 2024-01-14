LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# OpenCV is located in dependencies directory.
OPENCV_ROOT := $(LOCAL_PATH)/../../../../dependencies/opencv

OPENCV_CAMERA_MODULES := on
OPENCV_INSTALL_MODULES := on
OPENCV_LIB_TYPE := SHARED

include ${OPENCV_ROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := github_com_st235_documentscanner_utils_OpenCVHelper.cpp
LOCAL_CFLAGS += -mfloat-abi=softfp -mfpu=neon -std=c++11
LOCAL_ARM_NEON  := true
LOCAL_LDLIBS += -llog
LOCAL_MODULE := OpenCVDocumentScannerLib

include $(BUILD_SHARED_LIBRARY)
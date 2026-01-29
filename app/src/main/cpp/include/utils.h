//
// Created by Ken on 2025/11/30.
//

#ifndef ANDROID_MOD_MENU_BNM_UTILS_H
#define ANDROID_MOD_MENU_BNM_UTILS_H

#include <jni.h>
#include <string>

jboolean toJboolean(JNIEnv *env, jobject obj);

jint toJint(JNIEnv *env, jobject obj);

jobjectArray toJobjectArray(JNIEnv *env, const std::vector<std::string> &feats);

#endif //ANDROID_MOD_MENU_BNM_UTILS_H

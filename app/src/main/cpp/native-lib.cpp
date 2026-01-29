#include <jni.h>
#include <string>
#include <thread>
#include <vector>

#include "logger.h"
#include <BNM/Image.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <BNM/Loading.hpp>
#include "utils.h"


void OnLoaded();

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    BNM::Loading::AddOnLoadedEvent(OnLoaded);
    BNM::Loading::TryLoadByJNI(env);
    return JNI_VERSION_1_6;
}

// FeatureTypes: Toggle, Seekbar, Category
// Examples:
// Toggle:ToggleName:true
// Seekbar:SeekbarName:1_20_10
// Category:CategoryName
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_android_support_Menu_getFeatureList(JNIEnv *env, jobject thiz) {
    std::vector<std::string> feats = {
            "Toggle:Currencies",
            "Toggle:Promote Progress:true",
            "Seekbar:Reward:1_10",
    };
    return toJobjectArray(env, feats);
}

struct Feature {
    bool currencies{};
    bool promote{};
    int reward{};
};

Feature feature{false, true, 1};

extern "C" JNIEXPORT void JNICALL
Java_com_android_support_Menu_valueChange(
        JNIEnv *env,
        jobject thiz,
        jint featIdx,
        jstring featName,
        jobject value
) {
    // featIdx: index in feature list
    switch (featIdx) {
        case 0: {
            feature.currencies = toJboolean(env, value);
            break;
        }
        case 1: {
            feature.promote = toJboolean(env, value);
            break;
        }
        case 2: {
            feature.reward = toJint(env, value);
            break;
        }
        default:
            break;
    }
}


bool (*old_CurrenciesTryAdd)(void *instance, int type, int amount, void *param);

bool CurrenciesTryAdd(void *instance, int type, int amount, void *param) {
    return old_CurrenciesTryAdd(instance, type, amount * feature.reward, param);
}

bool (*old_CurrenciesSpend)(void *instance, int type, int value, void *param);

bool CurrenciesSpend(void *instance, int type, int value, void *param) {
    if (instance != nullptr) {
        if (feature.currencies) {
            CurrenciesTryAdd(instance, type, value, param);
            return true;
        }
    }
    return old_CurrenciesSpend(instance, type, value, param);
}

void (*old_Init)(void *instance, int level, int progress);

void new_Init(void *instance, int level, int progress) {
    if (instance != nullptr) {
        if (feature.promote) {
            progress = 1000;
        }
    }
    return old_Init(instance, level, progress);
}


// Example Game: [Horny Villa](https://www.nutaku.net/games/horny-villa/)
void OnLoaded() {
    LOGI("OnLoaded");
    auto AssemblyCSharp = BNM::Image("Assembly-CSharp");
    auto Currencies = BNM::Class("StripClub.Model", "Currencies", AssemblyCSharp);
    auto Spend = Currencies.GetMethod("Spend", 3);
    auto TryAdd = Currencies.GetMethod("TryAdd", 3);
    auto Promote = BNM::Class("StripClub.Model.Cards", "Promote", AssemblyCSharp);
    auto Init = Promote.GetMethod("Init");

    BNM::BasicHook(Spend, CurrenciesSpend, old_CurrenciesSpend);
    BNM::BasicHook(TryAdd, CurrenciesTryAdd, old_CurrenciesTryAdd);
    BNM::BasicHook(Init, new_Init, old_Init);
}
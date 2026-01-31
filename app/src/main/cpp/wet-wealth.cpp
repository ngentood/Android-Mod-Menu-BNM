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
#include <BNM/Property.hpp>
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
            "Toggle:Characters",
            "Seekbar:Reward:1_20",
            "Seekbar:Temptation:1_20",
    };
    return toJobjectArray(env, feats);
}

struct Feature {
    bool characters{};
    int reward{};
    int temptation{};
};

Feature feature{false, 1, 1};

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
            feature.characters = toJboolean(env, value);
            break;
        }
        case 1: {
            feature.reward = toJint(env, value);
            break;
        }
        case 2: {
            feature.temptation = toJint(env, value);
            break;
        }
        default:
            break;
    }
}

BNM::Class UserCharacterFactory{};
BNM::Class CharacterStaticUsecase{};
BNM::Class UserCharactersUsecase{};
BNM::Class CharacterStatic{};

void (*old_AddItem)(void *instance, void *item, int count);

void new_AddItem(void *instance, void *item, int count) {
    return old_AddItem(instance, item, count * feature.reward);
}

void (*old_AddCharacterTemptation)(void *instance, void *character, int added);

void new_AddCharacterTemptation(void *instance, void *character, int added) {
    return new_AddCharacterTemptation(instance, character, added * feature.temptation);
}

void (*old_Load)(void *instance);

void new_Load(void *instance) {
    old_Load(instance);
    if (feature.characters) {
        auto userCharacterFactory = static_cast<BNM::Field<BNM::IL2CPP::Il2CppObject *>>(UserCharacterFactory.GetField(
                "userCharacterFactory"))[instance]();
        auto characterStaticUsecase = static_cast<BNM::Field<BNM::IL2CPP::Il2CppObject *>>(CharacterStaticUsecase.GetField(
                "userCharacterFactory"))[userCharacterFactory]();
        auto characters = static_cast<BNM::Property<BNM::Structures::Mono::List<BNM::IL2CPP::Il2CppObject *> >>(UserCharacterFactory.GetProperty(
                "Characters"))[characterStaticUsecase]();
        BNM::Method<void> AddCharacterOrExperience = UserCharactersUsecase.GetMethod(
                "AddCharacterOrExperience")[instance];
        BNM::Property<int> Id = CharacterStatic.GetProperty("Id");
        for (int i = 0; i < characters.size; ++i) {
            AddCharacterOrExperience(Id[characters[i]](), 100);
        }
    }
}


// Example Game: [Wet Wealth](https://www.nutaku.net/games/wet-wealth/)
void OnLoaded() {
    LOGI("OnLoaded");
    auto AssemblyCSharp = BNM::Image("Assembly-CSharp");

    auto UserInventoryUsecase = BNM::Class("WetWealth.UserInventory", "UserInventoryUsecase",
                                           AssemblyCSharp);
    auto AddItem = UserInventoryUsecase.GetMethod("AddItem");

    UserCharacterFactory = BNM::Class("WetWealth.Characters", "UserCharacterFactory",
                                      AssemblyCSharp);
    CharacterStaticUsecase = BNM::Class("WetWealth.Characters", "CharacterStaticUsecase",
                                        AssemblyCSharp);
    CharacterStatic = BNM::Class("WetWealth.Characters", "CharacterStatic",
                                 AssemblyCSharp);
    UserCharactersUsecase = BNM::Class("WetWealth.Characters", "UserCharactersUsecase",
                                       AssemblyCSharp);
    auto AddCharacterTemptation = UserCharactersUsecase.GetMethod("AddCharacterTemptation");
    auto Load = UserCharactersUsecase.GetMethod("Load");


    BNM::BasicHook(AddItem, new_AddItem, old_AddItem);
    BNM::BasicHook(AddCharacterTemptation, new_AddCharacterTemptation, old_AddCharacterTemptation);
    BNM::BasicHook(Load, new_Load, old_Load);
}
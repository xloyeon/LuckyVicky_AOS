package com.example.luckyvickyaos

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class LuckyVickyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //초기화 코드
        KakaoSdk.init(this, this.getString(R.string.kakao_app_key))
    }
}
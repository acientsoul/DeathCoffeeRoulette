package com.death.coffeeroulette.kakao

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient

/**
 * 카카오 SDK 연동 관리자
 * - 카카오 로그인
 * - 카카오톡 친구 목록 조회 (단톡방 멤버 직접 조회는 API 미지원)
 */
object KakaoAuthManager {

    private const val TAG = "KakaoAuth"

    /**
     * 카카오 로그인
     */
    fun login(context: Context, onResult: (Boolean, String?) -> Unit) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
                onResult(false, error.message)
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공: ${token.accessToken}")
                onResult(true, null)
            }
        }

        // 카카오톡 설치 여부에 따라 로그인 방식 분기
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패", error)

                    // 의도적 로그인 취소는 웹 로그인으로 전환하지 않음
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        onResult(false, "사용자가 로그인을 취소했습니다")
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡 로그인 실패 시 카카오 계정으로 로그인
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡 로그인 성공")
                    onResult(true, null)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    /**
     * 카카오톡 친구 목록 조회
     * 반환: List<Pair<id, nickname>>
     */
    fun getFriends(onResult: (List<Pair<String, String>>?, String?) -> Unit) {
        TalkApiClient.instance.friends { friends, error ->
            if (error != null) {
                Log.e(TAG, "친구 목록 조회 실패", error)
                onResult(null, error.message)
            } else if (friends != null) {
                val friendList = friends.elements?.map { friend ->
                    Pair(friend.id.toString(), friend.profileNickname ?: "이름없음")
                } ?: emptyList()
                Log.i(TAG, "친구 ${friendList.size}명 조회 완료")
                onResult(friendList, null)
            }
        }
    }

    /**
     * 로그아웃
     */
    fun logout(onResult: (Boolean) -> Unit) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패", error)
                onResult(false)
            } else {
                Log.i(TAG, "로그아웃 성공")
                onResult(true)
            }
        }
    }

    /**
     * 로그인 상태 확인
     */
    fun checkLoginStatus(onResult: (Boolean) -> Unit) {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            onResult(error == null && tokenInfo != null)
        }
    }
}

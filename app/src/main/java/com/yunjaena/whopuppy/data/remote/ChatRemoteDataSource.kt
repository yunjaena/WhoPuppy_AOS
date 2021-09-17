package com.yunjaena.whopuppy.data.remote

import com.orhanobut.hawk.Hawk
import com.yunjaena.whopuppy.constant.ACCESS_TOKEN
import com.yunjaena.whopuppy.constant.PRODUCTION_SERVER_BASE_URL
import com.yunjaena.whopuppy.constant.PRODUCTION_SOCKET_URL
import com.yunjaena.whopuppy.data.ChatDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders

class ChatRemoteDataSource(
    private val stompClient: StompClient
) : ChatDataSource {
    private var session: StompSession? = null

    override suspend fun connectToChatSever() {
        session = coroutineScope {
            stompClient.connect(
                PRODUCTION_SOCKET_URL,
                customStompConnectHeaders = getHeader()
            )
        }
    }

    override suspend fun subscribeTopic(topic: String): Flow<StompFrame.Message> {
        return session?.subscribe(StompSubscribeHeaders(topic, customHeaders = getHeader()))!!
    }

    override suspend fun sendMessage(topic: String, message: String) {
        session?.send(
            StompSendHeaders(topic, customHeaders = getHeader()),
            FrameBody.Text(message)
        )
    }

    override suspend fun sendMessage(topic: String) {
        session?.send(StompSendHeaders(topic, customHeaders = getHeader()), null)
    }

    override suspend fun disconnectChatServer() {
        try {
            session?.disconnect()
        } catch (e: Exception) {

        }
    }

    private fun getHeader(): Map<String, String> {
        val accessToken = Hawk.get(ACCESS_TOKEN, "")
        return mapOf(
            "Authorization" to "Bearer $accessToken",
            "Origin" to PRODUCTION_SERVER_BASE_URL
        )
    }
}

package com.example.projetorole.repository

import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.CheckInDTO
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.safeRemoteCall

class CheckinNetworkRepository {
    suspend fun getMyCheckins(): List<CheckInDTO> = safeRemoteCall {
        val response: ApiResponse<List<CheckInDTO>> = getRemote("/api/me/checkins")
        if (response.success && response.data != null) response.data else emptyList()
    }
}
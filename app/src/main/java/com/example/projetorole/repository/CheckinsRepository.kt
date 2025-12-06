package com.example.projetorole.repository
import com.example.projetorole.network.ApiResponse
import com.example.projetorole.network.CheckInHistoryNetwork
import com.example.projetorole.network.getRemote
import com.example.projetorole.network.safeRemoteCall

class CheckinsRepository {

    suspend fun getHistorico(): List<CheckInHistoryNetwork> {
        return safeRemoteCall {
            val response: ApiResponse<List<CheckInHistoryNetwork>> = getRemote("/api/checkins/history")

            if (response.success && response.data != null) {
                response.data
            } else {
                emptyList()
            }
        }
    }
}

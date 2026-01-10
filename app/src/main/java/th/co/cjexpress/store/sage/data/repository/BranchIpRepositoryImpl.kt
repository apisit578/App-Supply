package th.co.cjexpress.store.sage.data.repository

import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.mapper.branchMaster.BranchIpMapper
import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.data.source.remote.BranchIpApi
import th.co.cjexpress.store.sage.domain.model.BranchIpModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.BranchIpRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import java.io.IOException

class BranchIpRepositoryImpl(
    private val branchIpApi: BranchIpApi,
    private val mapper: BranchIpMapper,
) : BranchIpRepository {
    override suspend fun checkBranchIP(ipAddress: String): Result<BranchIpModel> =
        try {
            val response = branchIpApi.getBranchIp(ipAddress)
            if (response != null) {
                val respModel =
                    response.bodyToObject(BranchIpResponse::class.java)
                        ?: throw Exception("ข้อมูลการตรวจสอบสาขาของเครื่องไม่ถูกต้อง")

                if (response.isSuccessful) {
                    val model = mapper.map(respModel)
                    Result.Success(model)
                } else {
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = respModel.errorCode ?: "",
                                type = ErrorType.ERROR,
                                message = respModel.errorMessage,
                            ),
                    )
                }
            } else {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.ERROR,
                            message = "ไม่ได้รับข้อมูลตอบกลับจากเซอร์วิส",
                        ),
                )
            }
        } catch (e: IOException) {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.ERROR,
                        message = "ไม่สามารถตรวจสอบสาขาของเครื่องได้ในเวลาที่กำหนด (${e.message})",
                    ),
            )
        } catch (e: Exception) {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.ERROR,
                        message = "เกิดข้อผิดพลาดในขั้นตอนการตรวจสอบสาขาของเครื่อง ${e.message}",
                    ),
            )
        }
}
